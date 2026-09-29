package distributor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import distributor.apicollection.OneOfAllApi;
import distributor.apicollection.OwenWilsonWowApi;
import distributor.apicollection.SunsetTimeApi;
import distributor.discrecord.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiHandlerTest
{
    private WebAdapter mockClient;
    private HttpResponse<String> mockResponse;

    @BeforeEach
    public void prepareMock()
    {
        mockClient = Mockito.mock(WebAdapter.class);
        mockResponse = Mockito.mock(HttpResponse.class);
    }

    @Test
    void sendLordOfRingsValidTest() throws IOException, InterruptedException
    {
        String[] query = {"name=Gandalf"};
        String mockResponseBody = getLordGandalfData();

        doReturn(mockResponse).when(mockClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        when(mockResponse.body()).thenReturn(mockResponseBody);

        ApiHandler apiHandler = new OneOfAllApi(mockClient);
        Note note = apiHandler.send(query);

        assertNotNull(note);
        assertEquals("The Lord of the Rings Api", note.source());
        assertEquals("\"Gandalf\"", note.data().get("docs").get(0).get("name").toString());
        assertEquals(parseJson(mockResponseBody), note.data());
    }
    @Test
    void sendLordOfRingsEmptyQueryValidTest() throws IOException, InterruptedException
    {
        String mockResponseBody = getLordFrodoData();

        doReturn(mockResponse).when(mockClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        when(mockResponse.body()).thenReturn(mockResponseBody);

        ApiHandler apiHandler = new OneOfAllApi(mockClient);
        Note note1 = apiHandler.send(new String[]{});
        Note note2 = apiHandler.send(null);

        assertNotNull(note1);
        assertNotNull(note2);
        assertEquals("The Lord of the Rings Api", note1.source());
        assertEquals("The Lord of the Rings Api", note2.source());
        assertEquals("\"Frodo Baggins\"", note1.data().get("docs").get(0).get("name").toString());
        assertEquals("\"Frodo Baggins\"", note2.data().get("docs").get(0).get("name").toString());
        assertEquals(parseJson(mockResponseBody), note1.data());
        assertEquals(parseJson(mockResponseBody), note2.data());
    }
    @Test
    void sendSunsetTimeValidTest() throws IOException, InterruptedException
    {
        String[] query = {"lat=44.09", "lng=12.67"};
        String mockResponseBody = getSunsetData();

        doReturn(mockResponse).when(mockClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        when(mockResponse.body()).thenReturn(mockResponseBody);

        ApiHandler apiHandler = new SunsetTimeApi(mockClient);
        Note note = apiHandler.send(query);

        assertNotNull(note);
        assertEquals("Sunset Sunrise Time Api", note.source());
        assertEquals(parseJson(mockResponseBody), note.data());
    }
    @Test
    void sendSunsetTimeEmptyQueryValidTest() throws IOException, InterruptedException
    {
        String mockResponseBody = getSunsetData();

        doReturn(mockResponse).when(mockClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        when(mockResponse.body()).thenReturn(mockResponseBody);

        ApiHandler apiHandler = new SunsetTimeApi(mockClient);
        Note note1 = apiHandler.send(new String[]{});
        Note note2 = apiHandler.send(null);

        assertNotNull(note1);
        assertNotNull(note2);
        assertEquals("Sunset Sunrise Time Api", note1.source());
        assertEquals("Sunset Sunrise Time Api", note2.source());
        assertEquals(parseJson(mockResponseBody), note1.data());
        assertEquals(parseJson(mockResponseBody), note2.data());
    }

    @Test
    void sendThrowIOExceptionTest() throws IOException, InterruptedException
    {
        doThrow(new IOException("Connection refused")).when(mockClient).
                send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

        ApiHandler apiHandler = new SunsetTimeApi(mockClient);

        IOException exception = assertThrows(IOException.class, () -> apiHandler.send(new String[]{}));
        assertEquals("Connection refused", exception.getMessage());
    }

    @Test
    void sendThrowIOExceptionNullMessageTest() throws IOException, InterruptedException
    {
        doThrow(new IOException()).when(mockClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

        ApiHandler apiHandler = new SunsetTimeApi(mockClient);

        IOException exception = assertThrows(IOException.class, () -> apiHandler.send(new String[]{}));
        assertEquals("The network connection to the website cannot be established", exception.getMessage());
    }

    @Test
    void sendResponseEmptyTest() throws IOException, InterruptedException
    {
        doReturn(mockResponse).when(mockClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        when(mockResponse.body()).thenReturn("");

        ApiHandler apiHandler = new SunsetTimeApi(mockClient);

        IOException exception = assertThrows(IOException.class, () -> apiHandler.send(new String[]{}));
        assertEquals("The website cannot return correct response", exception.getMessage());
    }

    @Test
    void sendResponseNullTest() throws IOException, InterruptedException
    {
        doReturn(mockResponse).when(mockClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        when(mockResponse.body()).thenReturn(null);

        ApiHandler apiHandler = new SunsetTimeApi(mockClient);

        IOException exception = assertThrows(IOException.class, () -> apiHandler.send(new String[]{}));
        assertEquals("The website cannot return correct response", exception.getMessage());
    }

    @Test
    void sendDefaultAddQueryTest() throws IOException, InterruptedException
    {
        String[] query = {"year=2011"};
        String mockResponseBody = getOwenData();

        doReturn(mockResponse).when(mockClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        when(mockResponse.body()).thenReturn(mockResponseBody);

        ApiHandler apiHandler = new OwenWilsonWowApi(mockClient);
        Note note = apiHandler.send(query);

        assertNotNull(note);
        assertEquals("Owen Wilson Wow Api", note.source());
        assertEquals(parseJson(mockResponseBody), note.data());
    }

    @Test
    void sendDefaultAddNullQueryTest() throws IOException, InterruptedException
    {
        String mockResponseBody = getOwenData();

        doReturn(mockResponse).when(mockClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        when(mockResponse.body()).thenReturn(mockResponseBody);

        ApiHandler apiHandler = new OwenWilsonWowApi(mockClient);
        Note note = apiHandler.send(null);

        assertNotNull(note);
        assertEquals("Owen Wilson Wow Api", note.source());
        assertEquals(parseJson(mockResponseBody), note.data());
    }

    @Test
    void sendDefaultAddEmptyQueryTest() throws IOException, InterruptedException
    {
        String mockResponseBody = getOwenData();

        doReturn(mockResponse).when(mockClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
        when(mockResponse.body()).thenReturn(mockResponseBody);

        ApiHandler apiHandler = new OwenWilsonWowApi(mockClient);
        Note note = apiHandler.send(new String[]{});

        assertNotNull(note);
        assertEquals("Owen Wilson Wow Api", note.source());
        assertEquals(parseJson(mockResponseBody), note.data());
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
    private String getLordGandalfData()
    {
        return """
        {"docs":[{"_id":"5cd99d4bde30eff6ebccfea0","name":"Gandalf","wikiUrl":"http://lotr.wikia.com//wiki/Gandalf","race":"Maiar","birth":"Before the the Shaping of Arda","gender":"Male","death":"January 253019 ,Battle of the Peak immortal","hair":"Grey, later white","height":null,"realm":null,"spouse":null}],"total":1,"limit":1000,"offset":0,"page":1,"pages":1}
        """;
    }
    private String getLordFrodoData()
    {
        return """
        {"docs":[{"_id":"5cd99d4bde30eff6ebccfc15","name":"Frodo Baggins","wikiUrl":"http://lotr.wikia.com//wiki/Frodo_Baggins","race":"Hobbit","birth":"22 September ,TA 2968","gender":"Male","death":"Unknown (Last sighting ,September 29 ,3021,) (,SR 1421,)","hair":"Brown","height":"1.06m (3'6\\")","realm":null,"spouse":null}],"total":1,"limit":1000,"offset":0,"page":1,"pages":1}
        """;
    }
    private String getSunsetData()
    {
        return """
        {"results":{"sunrise":"12:33:41 AM","sunset":"7:22:15 PM","solar_noon":"9:57:58 AM","day_length":"18:48:34","civil_twilight_begin":"11:00:03 PM","civil_twilight_end":"8:55:54 PM","nautical_twilight_begin":"12:00:01 AM","nautical_twilight_end":"12:00:01 AM","astronomical_twilight_begin":"12:00:01 AM","astronomical_twilight_end":"12:00:01 AM"},"status":"OK","tzid":"UTC"}
        """;
    }
}
