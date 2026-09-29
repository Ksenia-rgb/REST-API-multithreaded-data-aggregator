package distributor.apicollection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OneOfAllApiTest
{
    @Test
    void getNameTest()
    {
        OneOfAllApi api = new OneOfAllApi();
        assertEquals("The Lord of the Rings Api", api.getName());
    }
    @Test
    void getHttpTest()
    {
        OneOfAllApi api = new OneOfAllApi();
        assertEquals("https://the-one-api.dev/v2/character", api.getHttp());
    }
}
