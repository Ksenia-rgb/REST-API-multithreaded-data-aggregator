package distributor.respproc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import distributor.discrecord.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CsvProcessorTest
{
    private Path filePath;

    @TempDir
    private Path tempDir;

    @BeforeEach
    public void setTempFilePath()
    {
        filePath = tempDir.resolve("test.csv");
    }

    @Test
    void saveNewFileNotAppendTest() throws IOException
    {
        Note[] notes = {new Note(1, "Owen Wilson Wow Api",
                "17:15:00", parseJson(getOwenData()))};

        CsvProcessor processor = new CsvProcessor();
        processor.save(notes, filePath.toString(), false);

        assertTrue(Files.exists(filePath));
        assertTrue(Files.readString(filePath).contains("id,source,timestamp"));
        assertTrue(Files.readString(filePath).contains(getOwenHeader()));
        assertTrue(Files.readString(filePath).contains("1,\"Owen Wilson Wow Api\""));
    }

    @Test
    void saveNewFileAppendTest()
    {
        Note[] notes = {new Note(1, "Owen Wilson Wow Api",
                "17:15:00", parseJson(getOwenData()))};

        CsvProcessor processor = new CsvProcessor();

        assertThrows(IllegalArgumentException.class, () -> processor.save(
                notes, filePath.toString(), true
        ));
    }

    @Test
    void saveExistedFileNotAppendTest() throws IOException
    {
        Files.createFile(filePath);

        Note[] notes1 = {new Note(1, "Owen Wilson Wow Api",
                "17:15:00", parseJson(getOwenData()))};
        Note[] notes2 = {new Note(2, "The Lord of the Rings Api",
                "17:40:00", parseJson(getLordData()))};

        CsvProcessor processor = new CsvProcessor();
        processor.save(notes1, filePath.toString(), false);

        assertTrue(Files.exists(filePath));
        assertTrue(Files.readString(filePath).contains(getOwenHeader()));
        assertTrue(Files.readString(filePath).contains("1,\"Owen Wilson Wow Api\""));

        processor.save(notes2, filePath.toString(), false);

        assertFalse(Files.readString(filePath).contains(getOwenHeader()));
        assertTrue(Files.readString(filePath).contains(getLordHeader()));
        assertFalse(Files.readString(filePath).contains("1,\"Owen Wilson Wow Api\""));
        assertTrue(Files.readString(filePath).contains("2,\"The Lord of the Rings Api\""));
    }

    @Test
    void saveExistedFileAppendTest() throws IOException
    {
        Files.createFile(filePath);

        Note[] notes1 = {new Note(1, "Owen Wilson Wow Api",
                "17:15:00", parseJson(getOwenData()))};
        Note[] notes2 = {new Note(2, "The Lord of the Rings Api",
                "17:40:00", parseJson(getLordData()))};


        CsvProcessor processor = new CsvProcessor();
        processor.save(notes1, filePath.toString(), true);

        assertTrue(Files.exists(filePath));
        assertTrue(Files.readString(filePath).contains("1,\"Owen Wilson Wow Api\""));

        processor.save(notes2, filePath.toString(), true);

        assertTrue(Files.readString(filePath).contains(getOwenHeader()));
        assertTrue(Files.readString(filePath).contains(getLordHeader()));
        assertTrue(Files.readString(filePath).contains("1,\"Owen Wilson Wow Api\""));
        assertTrue(Files.readString(filePath).contains("2,\"The Lord of the Rings Api\""));
    }

    @Test
    void saveSingleTest() throws IOException
    {
        Note note1 = new Note(1, "Owen Wilson Wow Api",
                "17:15:00", parseJson(getOwenData()));
        Note note2 = new Note(2, "Owen Wilson Wow Api",
                "17:40:00", parseJson(getOwenData()));


        CsvProcessor processor = new CsvProcessor();

        assertDoesNotThrow(() -> processor.saveSingle(note1, filePath.toString(), false));
        assertDoesNotThrow(() -> processor.saveSingle(note2, filePath.toString(), true));

        assertTrue(Files.exists(filePath));
        assertTrue(Files.readString(filePath).contains("1,\"Owen Wilson Wow Api\""));
        assertTrue(Files.readString(filePath).contains("2,\"Owen Wilson Wow Api\""));
    }

    @Test
    @StdIo
    void outputAllTest(StdOut out) throws IOException
    {
        Note[] notes = {
                new Note(1, "Owen Wilson Wow Api",
                        "17:15:00", parseJson(getOwenData())),
                new Note(2, "The Lord of the Rings Api",
                        "17:40:00", parseJson(getLordData()))
        };

        CsvProcessor processor = new CsvProcessor();
        processor.save(notes, filePath.toString(), false);
        processor.output(filePath.toString());

        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains(getOwenHeader())));
        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains(getLordHeader())));
        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("1,\"Owen Wilson Wow Api\"")));
        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("2,\"The Lord of the Rings Api\"")));
    }

    @Test
    @StdIo
    void outputAllEmptyFileTest(StdOut out) throws IOException
    {
        Files.createFile(filePath);

        CsvProcessor processor = new CsvProcessor();
        processor.output(filePath.toString());

        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("empty")));
        assertFalse(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains(getOwenHeader())));
        assertFalse(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains(getLordHeader())));
    }

    @Test
    @StdIo
    void outputNameTest(StdOut out) throws IOException
    {
        Note[] notes = {
                new Note(1, "Owen Wilson Wow Api",
                        "17:15:00", parseJson(getOwenData())),
                new Note(2, "Owen Wilson Wow Api",
                        "17:35:00", parseJson(getOwenData())),
                new Note(3, "The Lord of the Rings Api",
                        "17:40:00", parseJson(getLordData()))
        };

        CsvProcessor processor = new CsvProcessor();
        processor.save(notes, filePath.toString(), false);
        processor.output(filePath.toString(), "Owen Wilson Wow Api");

        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains(getOwenHeader())));
        assertFalse(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains(getLordHeader())));

        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("1,\"Owen Wilson Wow Api\"")));
        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("2,\"Owen Wilson Wow Api\"")));
        assertFalse(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("3,\"The Lord of the Rings Api\"")));
    }

    @Test
    @StdIo
    void outputNameButNotSuitTest(StdOut out) throws IOException
    {
        Note[] notes = {
                new Note(1, "Owen Wilson Wow Api",
                        "17:15:00", parseJson(getOwenData())),
                new Note(2, "The Lord of the Rings Api",
                        "17:40:00", parseJson(getLordData()))
        };

        CsvProcessor processor = new CsvProcessor();
        processor.save(notes, filePath.toString(), false);
        processor.output(filePath.toString(), "Sunset Sunrise Time Api");

        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("empty")));
        assertFalse(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains(getOwenHeader())));
        assertFalse(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains(getLordHeader())));
    }

    @Test
    @StdIo
    void outputNameEmptyFileTest(StdOut out) throws IOException
    {
        Files.createFile(filePath);

        CsvProcessor processor = new CsvProcessor();
        processor.output(filePath.toString(), "Owen Wilson Wow Api");

        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("empty")));
    }

    @Test
    @StdIo
    void outputSingleTest(StdOut out) throws IOException
    {
        Note note = new Note(1, "Owen Wilson Wow Api",
                "17:15:00", parseJson(getOwenData()));

        CsvProcessor processor = new CsvProcessor();
        processor.outputSingle(note);

        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains(getOwenHeader())));
        assertTrue(Arrays.stream(out.capturedLines()).anyMatch(str -> str.contains("1,\"Owen Wilson Wow Api\"")));
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
    private String getOwenHeader()
    {
        return "data.movie,data.year,data.release_date,data.director,data.character,data.movie_duration,data.timestamp,data.full_line,\"data.current_wow_in_movie\",data.total_wows_in_movie,data.poster,data.video.1080p,data.video.720p,data.video.480p,data.video.360p,data.audio";
    }
    private String getLordHeader()
    {
        return "data.docs._id,data.docs.name,data.docs.wikiUrl,data.docs.race,data.docs.birth,data.docs.gender,data.docs.death,data.docs.hair,data.docs.height,data.docs.realm,data.docs.spouse,data.total,data.limit,data.offset,data.page,data.pages";
    }
}
