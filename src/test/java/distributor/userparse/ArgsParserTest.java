package distributor.userparse;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArgsParserTest
{
    @Test
    void constructLessArgumentsTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "The Lord of the Rings Api"};

        assertThrows(IllegalArgumentException.class, () -> new ArgsParser(args));
    }
    @Test
    void parseNamesValidTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "The Lord of the Rings Api",
                "The Lord of the Rings Api", "json", "3", "5", "4"};
        String[] res = {"Owen Wilson Wow Api", "The Lord of the Rings Api", "The Lord of the Rings Api"};

        ArgsParser parser = new ArgsParser(args);
        parser.parseNames();

        assertEquals(3, parser.apis.length);
        for (int i = 0; i < parser.apis.length; i++)
        {
            assertEquals(res[i], parser.apis[i].name());
            assertArrayEquals(new String[]{}, parser.apis[i].query());
        }
    }
    @Test
    void parseNamesIncorrectNameTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "Incorrect", "json", "3", "5", "4"};

        ArgsParser parser = new ArgsParser(args);
        assertThrows(IllegalArgumentException.class, parser::parseNames);
    }
    @Test
    void parseFormatValidTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "json", "3", "5", "4"};

        ArgsParser parser = new ArgsParser(args);
        parser.parseFormat();
        assertEquals("json", parser.format);
    }
    @Test
    void parseFormatIncorrectTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "xml", "3", "5", "4"};

        ArgsParser parser = new ArgsParser(args);
        assertThrows(IllegalArgumentException.class, parser::parseFormat);
    }
    @Test
    void parseThreadsValidTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "json", "3", "5", "4"};

        ArgsParser parser = new ArgsParser(args);
        parser.parseThreads();
        assertEquals(3, parser.threads);
    }
    @Test
    void parseThreadsNotIntTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "json", "threads 3", "5", "4"};

        ArgsParser parser = new ArgsParser(args);
        assertThrows(NumberFormatException.class, parser::parseThreads);
    }
    @Test
    void parseThreadsNegativeTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "json", "-3", "5", "4"};

        ArgsParser parser = new ArgsParser(args);
        assertThrows(IllegalArgumentException.class, parser::parseThreads);
    }
    @Test
    void parsePeriodValidTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "xml", "3", "5", "4"};

        ArgsParser parser = new ArgsParser(args);
        parser.parsePeriod();
        assertEquals(5, parser.period);
    }
    @Test
    void parsePeriodNotIntTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "json", "3", "period 5", "4"};

        ArgsParser parser = new ArgsParser(args);
        assertThrows(NumberFormatException.class, parser::parsePeriod);
    }
    @Test
    void parsePeriodNegativeTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "json", "3", "-5", "4"};

        ArgsParser parser = new ArgsParser(args);
        assertThrows(IllegalArgumentException.class, parser::parsePeriod);
    }
    @Test
    void parsePeriodZeroTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "json", "3", "0", "4"};

        ArgsParser parser = new ArgsParser(args);
        parser.parsePeriod();
        assertEquals(0, parser.period);
    }
    @Test
    void parseCountValidTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "xml", "3", "5", "4"};

        ArgsParser parser = new ArgsParser(args);
        parser.parseCount();
        assertEquals(4, parser.count);
    }
    @Test
    void parseCountNotIntTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "json", "3", "5", "count 4"};

        ArgsParser parser = new ArgsParser(args);
        assertThrows(NumberFormatException.class, parser::parseCount);
    }
    @Test
    void parseCountNegativeTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "json", "3", "5", "-4"};

        ArgsParser parser = new ArgsParser(args);
        assertThrows(IllegalArgumentException.class, parser::parseCount);
    }
    @Test
    void parseAllTest()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "json", "3", "5", "4"};

        ArgsParser parser = new ArgsParser(args);
        parser.parseAll();

        assertEquals(1, parser.apis.length);
        assertEquals("Owen Wilson Wow Api", parser.apis[0].name());
        assertEquals("json", parser.format);
        assertEquals(3, parser.threads);
        assertEquals(5, parser.period);
        assertEquals(4, parser.count);
    }

}
