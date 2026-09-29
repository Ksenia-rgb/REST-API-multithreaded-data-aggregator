package distributor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import distributor.apicollection.OwenWilsonWowApi;
import distributor.discrecord.ApiInfo;
import distributor.discrecord.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApiBdTest
{
    private ApiBd apiBd;
    private WebAdapter mockClient;
    private HttpResponse<String> mockResponse;

    @TempDir
    Path tempDir;

    @BeforeEach
    void prepareApiBd()
    {
        mockClient = Mockito.mock(WebAdapter.class);
        mockResponse = Mockito.mock(HttpResponse.class);

        HashMap<String, ApiHandler> apiHandlers = new HashMap<>();
        apiHandlers.put("Owen Wilson Wow Api", new OwenWilsonWowApi(mockClient));
        apiBd = new ApiBd(apiHandlers, DistributorMain.formatsProc);
    }

    @Test
    void executeValidTest() throws IOException, InterruptedException
    {
        doReturn(mockResponse).when(mockClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        when(mockResponse.body()).thenReturn(getOwenData());

        ApiInfo[] info = {new ApiInfo("Owen Wilson Wow Api", new String[]{})};

        Note[] notes = apiBd.execute(info);

        assertEquals(1, notes.length);
        assertEquals("Owen Wilson Wow Api", notes[0].source());
        verify(mockClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
    @Test
    void executeEmptyTest() throws IOException, InterruptedException
    {
        ApiInfo[] info = {};

        Note[] notes = apiBd.execute(info);

        assertEquals(0, notes.length);
        verify(mockClient, never()).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
    @Test
    void executeIncorrectNameTest() throws IOException, InterruptedException
    {
        ApiInfo[] info = {new ApiInfo("Incorrect Api", new String[]{})};

        IllegalArgumentException exeption = assertThrows(IllegalArgumentException.class, () -> apiBd.execute(info));
        assertEquals("Incorrect api name", exeption.getMessage());

        verify(mockClient, never()).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
    @Test
    void saveTest() throws IOException
    {
        Path filePath = tempDir.resolve("test.json");

        Note[] notes = {new Note(1, "Owen Wilson Wow Api",
                "17:15:00", parseJson(getOwenData()))};

        apiBd.save(notes, "json", filePath.toString(), false);

        assertTrue(Files.exists(filePath));
        assertTrue(Files.readString(filePath).contains("\"id\" : 1"));
    }

    @Test
    void saveEmptyTest() throws IOException
    {
        Path filePath = tempDir.resolve("test.json");

        apiBd.save(new Note[]{}, "json", filePath.toString(), false);

        assertFalse(Files.exists(filePath));
    }

    @Test
    void saveSingleTest() throws IOException
    {
        Path filePath = tempDir.resolve("test.json");

        Note note = new Note(1, "Owen Wilson Wow Api",
                "17:15:00", parseJson(getOwenData()));

        apiBd.saveSingle(note, "json", filePath.toString(), false);

        assertTrue(Files.exists(filePath));
        assertTrue(Files.readString(filePath).contains("\"id\" : 1"));
    }

    @Test
    @StdIo
    void outputTest(StdOut out) throws IOException
    {
        Path filePath = tempDir.resolve("test.json");

        Note[] notes = {
                new Note(1, "Owen Wilson Wow Api",
                        "17:15:00", parseJson(getOwenData())),
                new Note(2, "The Lord of the Rings Api",
                        "17:40:00", parseJson(getLordData()))
        };

        apiBd.save(notes, "json", filePath.toString(), false);
        apiBd.output("json", filePath.toString(), "");

        assertTrue(out.capturedString().isEmpty());

        apiBd.output("json", filePath.toString(), "The Lord of the Rings Api");

        assertFalse(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("\"id\" : 1")));
        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("\"id\" : 2")));

        apiBd.output("json", filePath.toString(), "all");

        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("\"id\" : 1")));
        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("\"id\" : 2")));

    }

    private JsonNode parseJson(String response)
    {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(response);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    private String getOwenData()
    {
        return """
        [{"movie":"Hall Pass","year":2011,"release_date":"2011-02-25","director":"Peter Farrelly and Bobby Farrelly","character":"Rick Mills","movie_duration":"01:51:35","timestamp":"00:46:55","full_line":"Wow. This is awkward. I feel like I'm at my first junior high mixer. You know? When you don't know what to say.","current_wow_in_movie":3,"total_wows_in_movie":6,"poster":"https://images.ctfassets.net/bs8ntwkklfua/6jFEUPmYiKifaTuC2cugm8/22087834d091445fc9393cdd9163a901/Hall_Pass_Poster.jpg","video":{"1080p":"https://videos.ctfassets.net/bs8ntwkklfua/4QcL02MHJ8ApVkbfN8cP6E/264c28c1e9195d87f0206e143c5ca54a/Hall_Pass_Wow_3_1080p.mp4","720p":"https://videos.ctfassets.net/bs8ntwkklfua/15h0sMoIhdeaPDB8qSsUN9/36245f66352b595dc40bc4d9903fa5b3/Hall_Pass_Wow_3_720p.mp4","480p":"https://videos.ctfassets.net/bs8ntwkklfua/74fQiVcwuT7ePQemGC7ih4/b102922c97c9ff38f47268d648628a22/Hall_Pass_Wow_3_480p.mp4","360p":"https://videos.ctfassets.net/bs8ntwkklfua/7mSGl1rSVtGdfSacwnKVsu/e7ac36e5684f6b64978987d2f68c43db/Hall_Pass_Wow_3_360p.mp4"},"audio":"https://assets.ctfassets.net/bs8ntwkklfua/2NBIVPDF4o7cy0epTvPOwR/406cd5c17e9b01511f1e350bb96df352/Hall_Pass_Wow_3.mp3"}]
        """;
    }
    private String getLordData()
    {
        return """
        {"docs":[{"_id":"5cd99d4bde30eff6ebccfea0","name":"Gandalf","wikiUrl":"http://lotr.wikia.com//wiki/Gandalf","race":"Maiar","birth":"Before the the Shaping of Arda","gender":"Male","death":"January 253019 ,Battle of the Peak immortal","hair":"Grey, later white","height":null,"realm":null,"spouse":null}],"total":1,"limit":1000,"offset":0,"page":1,"pages":1}
        """;
    }
}
