package distributor.discrecord;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataInputTest
{
    @Test
    void dataWithAppendTest()
    {
        ApiInfo info = new ApiInfo("Sunset Sunrise Time Api", new String[]{});
        ApiInfo[] apis = new ApiInfo[]{info};
        DataInput data = new DataInput(apis, "json", "path", true, "all", 3, 4, 5);
        DataInput dataNew = data.withAppend(false);
        assertTrue(data.append());
        assertFalse(dataNew.append());
    }
}
