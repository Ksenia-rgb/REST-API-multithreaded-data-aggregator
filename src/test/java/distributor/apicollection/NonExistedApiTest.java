package distributor.apicollection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NonExistedApiTest
{
    @Test
    void getNameTest()
    {
        NonExistedApi api = new NonExistedApi();
        assertEquals("Non Existed Api", api.getName());
    }
    @Test
    void getHttpTest()
    {
        NonExistedApi api = new NonExistedApi();
        assertEquals("https://non-existed-api", api.getHttp());
    }
}
