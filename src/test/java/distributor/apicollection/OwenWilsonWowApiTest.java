package distributor.apicollection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OwenWilsonWowApiTest
{
    @Test
    void getNameTest()
    {
        OwenWilsonWowApi api = new OwenWilsonWowApi();
        assertEquals("Owen Wilson Wow Api", api.getName());
    }
    @Test
    void getHttpTest()
    {
        OwenWilsonWowApi api = new OwenWilsonWowApi();
        assertEquals("https://owen-wilson-wow-api.onrender.com/wows/random/", api.getHttp());
    }
}
