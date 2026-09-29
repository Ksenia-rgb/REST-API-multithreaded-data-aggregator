package distributor;

import distributor.discrecord.ApiInfo;
import distributor.discrecord.Note;

import java.io.IOException;
import java.util.HashMap;

public class ApiBd
{
    private final HashMap<String, ApiHandler> apiHandlerMap;
    private final HashMap<String, ResponseProcessor> formatProcessorMap;

    public ApiBd(HashMap<String, ApiHandler> apiHandlerMap, HashMap<String, ResponseProcessor> formatProcessorMap)
    {
        this.apiHandlerMap = apiHandlerMap;
        this.formatProcessorMap = formatProcessorMap;
    }
    public Note[] execute(final ApiInfo[] apis)
            throws IOException, InterruptedException
    {
        Note[] notes = new Note[apis.length];
        for (int i = 0; i < apis.length; i++) {
            if (!apiHandlerMap.containsKey(apis[i].name())) {
                throw new IllegalArgumentException("Incorrect api name");
            }
            notes[i] = executeSingle(apis[i]);
        }
        return notes;
    }
    public void save(final Note[] notes, final String format, final String path, final boolean append) throws IOException
    {
        if (notes.length == 0) {
            return;
        }
        formatProcessorMap.get(format).save(notes, path, append);
    }
    public void output(final String format, final String path, final String apiName) throws IOException
    {
        if (apiName.isEmpty()) {
            return;
        }
        if (apiName.equals("all")) {
            formatProcessorMap.get(format).output(path);
            return;
        }
        formatProcessorMap.get(format).output(path, apiName);
    }
    public Note executeSingle(final ApiInfo api) throws IOException, InterruptedException
    {
        ApiHandler apiHandler = apiHandlerMap.get(api.name());
        return apiHandler.send(api.query());
    }
    public void saveSingle(final Note note, final String format, final String path, final boolean append) throws IOException
    {
        formatProcessorMap.get(format).saveSingle(note, path, append);
    }
}
