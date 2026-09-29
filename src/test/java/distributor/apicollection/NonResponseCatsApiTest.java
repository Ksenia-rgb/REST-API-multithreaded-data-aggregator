package distributor.apicollection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NonResponseCatsApiTest
{
    @Test
    void getNameTest()
    {
        NonResponseCatsApi api = new NonResponseCatsApi();
        assertEquals("Non Response Cats Api", api.getName());
    }
    @Test
    void getHttpTest()
    {
        NonResponseCatsApi api = new NonResponseCatsApi();
        assertEquals("https://http.cat/100", api.getHttp());
    }
}
