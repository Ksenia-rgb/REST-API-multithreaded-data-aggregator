package distributor;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistributorMainTest
{
    @Test
    @StdIo
    void mainIncorrectArgsTest(StdOut out)
    {
        String[] args = {"incorrect"};
        DistributorMain.main(args);

        assertEquals("Incorrect mode\r\n", out.capturedString());
    }
    @Test
    @StdIo
    void mainEmptyArgsTest(StdOut out)
    {
        String[] args = {};
        DistributorMain.main(args);

        assertEquals("Error: enter working mode: automatic or interactive\r\n", out.capturedString());
    }
}
