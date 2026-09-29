package distributor.apicollection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SunsetTimeApiTest
{
    @Test
    void getNameTest()
    {
        SunsetTimeApi api = new SunsetTimeApi();
        assertEquals("Sunset Sunrise Time Api", api.getName());
    }
    @Test
    void getHttpTest()
    {
        SunsetTimeApi api = new SunsetTimeApi();
        assertEquals("https://api.sunrise-sunset.org/json", api.getHttp());
    }
}
