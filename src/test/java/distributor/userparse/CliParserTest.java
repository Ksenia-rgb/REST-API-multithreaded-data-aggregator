package distributor.userparse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

import java.util.Scanner;

class CliParserTest
{
    private CliParser parser;

    @BeforeEach
    public void prepare()
    {
        Scanner scanner = new Scanner(System.in);
        parser = new CliParser(scanner);
    }

    @Test
    @StdIo("1 name=Gandalf")
    void parseNamesValidTest()
    {
        parser.parseNames();
        assertEquals(1, parser.apis.length);
        assertEquals("The Lord of the Rings Api", parser.apis[0].name());
        assertArrayEquals(new String[]{"name=Gandalf"}, parser.apis[0].query());
    }
    @Test
    @StdIo("1")
    void parseNamesValidEmptyQueryTest()
    {
        parser.parseNames();
        assertEquals(1, parser.apis.length);
        assertEquals("The Lord of the Rings Api", parser.apis[0].name());
        assertEquals(0, parser.apis[0].query().length);
    }
    @Test
    @StdIo("6")
    void parseNamesValidBreakTest()
    {
        parser.parseNames();
        assertEquals(0, parser.apis.length);
    }
    @Test
    @StdIo("api\n1 name=Gandalf")
    void parseNamesNonIntTest(StdOut out)
    {
        parser.parseNames();
        String outRes = """
                Choose api for request, enter every api with query on new line:\r
                1: The Lord of the Rings Api\r
                2: Owen Wilson Wow Api\r
                3: Sunset Sunrise Time Api\r
                4: Non Response Cats Api\r
                5: Non Existed Api\r
                6: End\r
                Incorrect API index, try again\r
                """;

        assertEquals(outRes, out.capturedString());
        assertEquals(1, parser.apis.length);
        assertEquals("The Lord of the Rings Api", parser.apis[0].name());
        assertArrayEquals(new String[]{"name=Gandalf"}, parser.apis[0].query());
    }
    @Test
    @StdIo("-1\n20")
    void parseNamesIncorrectIntValueTest(StdOut out)
    {
        parser.parseNames();
        String outRes = """
                Choose api for request, enter every api with query on new line:\r
                1: The Lord of the Rings Api\r
                2: Owen Wilson Wow Api\r
                3: Sunset Sunrise Time Api\r
                4: Non Response Cats Api\r
                5: Non Existed Api\r
                6: End\r
                Incorrect API index, try again\r
                Incorrect API index, try again\r
                """;

        assertEquals(outRes, out.capturedString());
    }
    @Test
    @StdIo("")
    void parseNamesEmptyTest()
    {
        parser.parseNames();
        assertEquals(0, parser.apis.length);
    }
    @Test
    @StdIo("1")
    void parseFormatValidJsonTest()
    {
        parser.parseFormat();

        assertEquals("json", parser.format);
    }
    @Test
    @StdIo("2")
    void parseFormatValidCsvTest()
    {
        parser.parseFormat();

        assertEquals("csv", parser.format);
    }
    @Test
    @StdIo("format\n1")
    void parseFormatNonIntTest(StdOut out)
    {
        parser.parseFormat();

        String outRes = """
                Enter save format:\r
                1: json\r
                2: csv\r
                Incorrect format, try again\r
                """;

        assertEquals(outRes, out.capturedString());
        assertEquals("json", parser.format);
    }
    @Test
    @StdIo("-1\n20")
    void parseFormatIncorrectValueTest(StdOut out)
    {
        parser.parseFormat();

        String outRes = """
                Enter save format:\r
                1: json\r
                2: csv\r
                Incorrect format, try again\r
                Incorrect format, try again\r
                """;

        assertEquals(outRes, out.capturedString());
        assertNull(parser.format);
    }
    @Test
    @StdIo("")
    void parseFormatEmptyTest()
    {
        parser.parseFormat();

        assertNull(parser.format);
    }
    @Test
    @StdIo("1\n1")
    void parsePathValidDefaultTest()
    {
        parser.parseFormat();
        parser.parsePath();

        assertEquals("json", parser.format);
        assertEquals("src/main/resources/jsonSave.json", parser.path);
    }
    @Test
    @StdIo("2\nsrc/main/resources/myOwnCsv.csv")
    void parsePathValidCustomTest()
    {
        parser.parseFormat();
        parser.parsePath();

        assertEquals("csv", parser.format);
        assertEquals("src/main/resources/myOwnCsv.csv", parser.path);
    }
    @Test
    @StdIo("")
    void parsePathEmptyTest()
    {
        parser.parsePath();

        assertEquals(0, parser.path.length());
    }
    @Test
    @StdIo("1")
    void parseAppendValidTest()
    {
        parser.parseAppend();

        assertTrue(parser.append);
    }
    @Test
    @StdIo("append\n2")
    void parseAppendNonIntTest(StdOut out)
    {
        parser.parseAppend();

        String outRes = """
                Enter file operating mode:\r
                1: append to existing file\r
                2: rewrite or create new file\r
                Incorrect append value, try again\r
                """;

        assertEquals(outRes, out.capturedString());
        assertFalse(parser.append);
    }
    @Test
    @StdIo("32")
    void parseAppendIncorrectValueTest(StdOut out)
    {
        parser.parseAppend();

        String outRes = """
                Enter file operating mode:\r
                1: append to existing file\r
                2: rewrite or create new file\r
                Incorrect append value, try again\r
                """;

        assertEquals(outRes, out.capturedString());
        assertFalse(parser.append);
    }
    @Test
    @StdIo("")
    void parseAppendEmptyTest()
    {
        parser.parseAppend();

        assertFalse(parser.append);
    }
    @Test
    @StdIo("0")
    void parseOutputValidZeroTest()
    {
        parser.parseOutput();

        assertEquals("", parser.output);
    }
    @Test
    @StdIo("6")
    void parseOutputValidAllTest()
    {
        parser.parseOutput();

        assertEquals("all", parser.output);
    }
    @Test
    @StdIo("1")
    void parseOutputValidBaseTest()
    {
        parser.parseOutput();

        assertEquals("The Lord of the Rings Api", parser.output);
    }
    @Test
    @StdIo("output\n1")
    void parseOutputNonIntTest(StdOut out)
    {
        parser.parseOutput();
        String outRes = """
                Enter console output mode:\r
                0: Not output\r
                1: The Lord of the Rings Api\r
                2: Owen Wilson Wow Api\r
                3: Sunset Sunrise Time Api\r
                4: Non Response Cats Api\r
                5: Non Existed Api\r
                6: Output all\r
                Incorrect output value, try again\r
                """;

        assertEquals(outRes, out.capturedString());
        assertEquals("The Lord of the Rings Api", parser.output);
    }
    @Test
    @StdIo("-100\n393920")
    void parseOutputIncorrectValueTest(StdOut out)
    {
        parser.parseOutput();
        String outRes = """
                Enter console output mode:\r
                0: Not output\r
                1: The Lord of the Rings Api\r
                2: Owen Wilson Wow Api\r
                3: Sunset Sunrise Time Api\r
                4: Non Response Cats Api\r
                5: Non Existed Api\r
                6: Output all\r
                Incorrect output value, try again\r
                Incorrect output value, try again\r
                """;

        assertEquals(outRes, out.capturedString());
        assertNull(parser.output);
    }
    @Test
    @StdIo("")
    void parseOutputEmptyTest()
    {
        parser.parseOutput();

        assertNull(parser.output);
    }
    @Test
    @StdIo("3")
    void parseThreadsValidTest()
    {
        parser.parseThreads();

        assertEquals(3, parser.threads);
    }
    @Test
    @StdIo("thread")
    void parseThreadsNonIntTest(StdOut out)
    {
        parser.parseThreads();
        String outRes = """
                Enter threads count:\r
                Incorrect threads value, try again\r
                """;

        assertEquals(outRes, out.capturedString());
        assertEquals(0, parser.threads);
    }
    @Test
    @StdIo("-1\n-364")
    void parseThreadsIncorrectValueTest(StdOut out)
    {
        parser.parseThreads();
        String outRes = """
                Enter threads count:\r
                Incorrect threads value, try again\r
                Incorrect threads value, try again\r
                """;

        assertEquals(outRes, out.capturedString());
        assertEquals(0, parser.threads);
    }
    @Test
    @StdIo("")
    void parseThreadsEmptyTest()
    {
        parser.parseOutput();

        assertEquals(0, parser.threads);
    }
    @Test
    @StdIo("10")
    void parsePeriodValidTest()
    {
        parser.parsePeriod();

        assertEquals(10, parser.period);
    }
    @Test
    @StdIo("period")
    void parsePeriodNonIntTest(StdOut out)
    {
        parser.parsePeriod();
        String outRes = """
                Enter execute period:\r
                Incorrect period value, try again\r
                """;

        assertEquals(outRes, out.capturedString());
        assertEquals(0, parser.period);
    }
    @Test
    @StdIo("-1\n-565")
    void parsePeriodIncorrectValueTest(StdOut out)
    {
        parser.parsePeriod();
        String outRes = """
                Enter execute period:\r
                Incorrect period value, try again\r
                Incorrect period value, try again\r
                """;

        assertEquals(outRes, out.capturedString());
        assertEquals(0, parser.period);
    }
    @Test
    @StdIo("")
    void parsePeriodEmptyTest()
    {
        parser.parsePeriod();

        assertNull(parser.output);
    }
    @Test
    @StdIo("1 race=Human\n6\n1\n1\n2\n0\n3\n10")
    void parseAllTest()
    {
        parser.parseAll();

        assertEquals(1, parser.apis.length);
        assertEquals("The Lord of the Rings Api", parser.apis[0].name());
        assertArrayEquals(new String[]{"race=Human"}, parser.apis[0].query());
        assertEquals("json", parser.format);
        assertEquals("src/main/resources/jsonSave.json", parser.path);
        assertFalse(parser.append);
        assertEquals("", parser.output);
        assertEquals(3, parser.threads);
        assertEquals(10, parser.period);
    }
}
