package distributor.respproc;

import com.fasterxml.jackson.core.util.InternalJacksonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import distributor.discrecord.Note;
import distributor.ResponseProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CsvProcessor implements ResponseProcessor
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
        List<JsonNode> alignedNodeList = makeCsv(notes);
        if (append) {
            alignedNodeList.addAll(0, readCsvSynch(file));
        }
        try (FileOutputStream writer = new FileOutputStream(file)) {
            writeCsvSynch(writer, alignedNodeList);
        }
    }
    @Override
    public void output(final String path) throws IOException
    {
        File file = new File(path);
        createFileSynch(file);

        List<JsonNode> readNodes = readCsvSynch(file);
        if (readNodes.isEmpty()) {
            printEmptySynch();
            return;
        }
        writeCsvSynch(System.out, readNodes);
    }
    @Override
    public void output(final String path, final String name) throws IOException
    {
        File file = new File(path);
        createFileSynch(file);

        List<JsonNode> readNodes = readCsvSynch(file);
        List<JsonNode> apiNodes = new ArrayList<>();
        List<String> removeFields = new ArrayList<>();
        int first = 0;
        while (first < readNodes.size() && !readNodes.get(first).get("source").asText().equals(name)) {
            first++;
        }
        if (first == readNodes.size()) {
            printEmptySynch();
            return;
        }
        JsonNode node = readNodes.get(first);
        Iterator<String> iter = node.fieldNames();
        while (iter.hasNext()) {
            String field = iter.next();
            if (node.get(field).asText().isEmpty()) {
                removeFields.add(field);
            }
        }
        ((ObjectNode) node).remove(removeFields);
        apiNodes.add(node);

        for (int i = first + 1; i < readNodes.size(); i++) {
            if (readNodes.get(i).get("source").asText().equals(name)) {
                JsonNode nodeAnother = readNodes.get(i);
                ((ObjectNode) nodeAnother).remove(removeFields);
                apiNodes.add(nodeAnother);
            }
        }
        writeCsvSynch(System.out, apiNodes);
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
        writeCsvSynch(System.out, makeCsv(notes));
    }
    private List<JsonNode> makeCsv(final Note[] notes) throws IOException
    {
        ArrayList<JsonNode> alignedNodeList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (Note note : notes) {
            String fullNodeString = mapper.writeValueAsString(note);
            JsonNode fullNode = mapper.readTree(fullNodeString);
            alignedNodeList.add(mapper.createObjectNode());
            traverseJson(fullNode, "", alignedNodeList.getLast());
        }
        return alignedNodeList;
    }
    private List<JsonNode> readCsvSynch(final File file) throws IOException
    {
        if (Files.size(file.toPath()) == 0) {
            return new ArrayList<>(){};
        }

        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();

        fileLock.readLock().lock();
        try (MappingIterator<JsonNode> nodeLines = csvMapper.readerFor(JsonNode.class)
                .with(csvSchema)
                .readValues(file)) {
            return nodeLines.readAll();
        } finally {
            fileLock.readLock().unlock();
        }
    }
    private void writeCsvSynch(OutputStream out, final List<JsonNode> nodes) throws IOException
    {
        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        for (JsonNode node : nodes) {
            Iterator<String> iter = node.fieldNames();
            while (iter.hasNext()) {
                String fieldName = iter.next();
                if (!csvSchemaBuilder.hasColumn(fieldName)) {
                    csvSchemaBuilder.addColumn(fieldName);
                }
            }
        }
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        ObjectWriter objectWriter = csvMapper.writer(csvSchema);
        try {
            fileLock.writeLock().lock();
            consoleLock.writeLock().lock();
            objectWriter.writeValue(out, nodes);
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
    private void traverseJson(final JsonNode root, final String parent, final JsonNode alignedNode)
    {
        if (root.isObject()) {
            Iterator<String> fieldNames = root.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode fieldValue = root.get(fieldName);
                String newParent = parent;
                if (!newParent.isEmpty()) {
                    newParent += ".";
                }
                newParent += fieldName;
                traverseJson(fieldValue, newParent, alignedNode);
            }
        } else if (root.isArray()) {
            ArrayNode array = (ArrayNode) root;
            for (int i = 0; i < array.size(); i++) {
                JsonNode arrayValue = array.get(i);
                traverseJson(arrayValue, parent, alignedNode);
            }
        } else {
            String value = root.asText();
            ((ObjectNode) alignedNode).put(parent, value);
        }
    }
}
