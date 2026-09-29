package distributor;

import distributor.discrecord.Note;

import java.io.IOException;

public interface ResponseProcessor
{
    void save(final Note[] notes, final String path, final boolean append) throws IOException;
    void output(final String path) throws IOException;
    void output(final String path, final String apiName) throws  IOException;
    void saveSingle(final Note note, final String path, final boolean append) throws IOException;
    void outputSingle(final Note note) throws IOException;
}
