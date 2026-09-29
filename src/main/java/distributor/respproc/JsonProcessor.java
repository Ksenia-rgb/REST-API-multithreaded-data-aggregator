package distributor.respproc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import distributor.discrecord.Note;
import distributor.ResponseProcessor;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonProcessor implements ResponseProcessor
{
    private static final ReentrantReadWriteLock fileLock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock consoleLock = new ReentrantReadWriteLock();

    @Override
    public void save(final Note[] notes, final String path, final boolean append) throws IOException
    {
        File file = new File(path);
        if (append && !checkFileExistSynch(file)) {
            throw new IllegalArgumentException("File not exist, can not be append");
        }
        createFileSynch(file);

        List<Note> resultNotes = new ArrayList<>(List.of(notes));
        if (append) {
            resultNotes.addAll(0, readJsonSynch(file));
        }

        try (FileOutputStream writer = new FileOutputStream(file)) {
            JsonNode resultNode = makeJson(resultNotes.toArray(new Note[0]));
            writeJsonSynch(writer, resultNode);
        }
    }
    @Override
    public void output(final String path) throws IOException
    {
        File file = new File(path);
        createFileSynch(file);

        List<Note> readNotes = readJsonSynch(file);
        if (readNotes.isEmpty()) {
            printEmptySynch();
            return;
        }
        JsonNode readNode = makeJson(readNotes.toArray(new Note[0]));
        writeJsonSynch(System.out, readNode);
    }
    @Override
    public void output(final String path, final String name) throws IOException
    {
        File file = new File(path);
        createFileSynch(file);

        List<Note> readNotes = readJsonSynch(file);
        List<Note> apiNotes = new ArrayList<>();
        for (Note note : readNotes) {
            if (note.source().equals(name)) {
                apiNotes.add(note);
            }
        }
        if (apiNotes.isEmpty()) {
            printEmptySynch();
            return;
        }
        JsonNode apiNode = makeJson(apiNotes.toArray(new Note[0]));
        writeJsonSynch(System.out, apiNode);
    }
    @Override
    public void saveSingle(final Note note, final String path, final boolean append) throws IOException
    {
        Note[] notes = new Note[] {note};
        save(notes, path, append);
    }
    @Override
    public void outputSingle(final Note note) throws IOException
    {
        Note[] notes = new Note[] {note};
        writeJsonSynch(System.out, makeJson(notes));
    }
    private JsonNode makeJson(final Note[] notes) throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(notes);
        return objectMapper.readTree(json);
    }
    private List<Note> readJsonSynch(final File file) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        fileLock.readLock().lock();
        try {
            if (Files.size(file.toPath()) == 0) {
                return new ArrayList<>() {};
            }
            return mapper.readValue(file, new TypeReference<>() {});
        } catch (IOException e) {
            throw new IOException("Json error empty notes");
        } finally {
            fileLock.readLock().unlock();
        }
    }
    private void writeJsonSynch(OutputStream out, final JsonNode node) throws IOException
    {
        fileLock.writeLock().lock();
        consoleLock.writeLock().lock();
        try {
            out.write(node.toPrettyString().getBytes());
        } finally {
            fileLock.writeLock().unlock();
            consoleLock.writeLock().unlock();
        }
    }
    private boolean createFileSynch(final File file) throws IOException
    {
        fileLock.writeLock().lock();
        try {
            return file.createNewFile();
        } finally {
            fileLock.writeLock().unlock();
        }
    }
    private boolean checkFileExistSynch(final File file)
    {
        fileLock.writeLock().lock();
        boolean res = file.exists();
        fileLock.writeLock().unlock();
        return res;
    }
    private void printEmptySynch()
    {
        consoleLock.writeLock().lock();
        System.out.println("empty");
        consoleLock.writeLock().unlock();
    }
}
