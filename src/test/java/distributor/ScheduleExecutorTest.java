package distributor;

import distributor.discrecord.ApiInfo;
import distributor.discrecord.DataInput;
import distributor.userparse.ArgsParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.*;

class ScheduleExecutorTest
{
    private DataInput data;
    private ApiBd apiBd;
    private ScheduledExecutorService mockService;

    @BeforeEach
    public void prepareExecutorArgs()
    {
        data = makeData();
        apiBd = new ApiBd(DistributorMain.apiHandlers, DistributorMain.formatsProc);
        mockService = Mockito.mock(ScheduledExecutorService.class);
    }

    @Test
    void runConstructNegativePeriodTest()
    {
        assertThrows(IllegalArgumentException.class, () -> new ScheduleExecutor(1, -20, data, apiBd));
    }

    @Test
    void runAutomaticEmptyTest() throws IOException, ExecutionException, InterruptedException
    {
        DataInput dataEmpty = makeEmptyData();
        ScheduleExecutor executor = new ScheduleExecutor(1, 10, dataEmpty, apiBd, mockService);
        executor.runAutomatic(1);

        verify(mockService, never()).schedule(any(Runnable.class), anyLong(), any());
    }

    @Test
    void runAutomaticZeroCountTest() throws IOException, ExecutionException, InterruptedException
    {
        ScheduleExecutor executor = new ScheduleExecutor(1, 10, data, apiBd, mockService);
        executor.runAutomatic(0);

        verify(mockService, never()).schedule(any(Runnable.class), anyLong(), any());
    }

    @Test
    void runAutomaticValidTest() throws ExecutionException, InterruptedException, IOException
    {
        ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
        doReturn(mockFuture).when(mockService).schedule(any(Runnable.class), anyLong(), any());
        doReturn(null).when(mockFuture).get();

        ScheduleExecutor executor = new ScheduleExecutor(2, 5, data, apiBd, mockService);
        executor.runAutomatic(2);

        verify(mockService, times(4)).schedule(any(Runnable.class), anyLong(), any());
        verify(mockFuture, times(4)).get();
    }

    @Test
    void runAutomaticOnceTest() throws ExecutionException, InterruptedException, IOException
    {
        ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);
        doReturn(mockFuture).when(mockService).schedule(any(Runnable.class), anyLong(), any());
        doReturn(null).when(mockFuture).get();

        ScheduleExecutor executor = new ScheduleExecutor(2, 5, data, apiBd, mockService);
        executor.runAutomatic(1);

        verify(mockService, times(2)).schedule(any(Runnable.class), anyLong(), any());
        verify(mockFuture, times(2)).get();
    }

    @Test
    void runInteractiveEmptyTest() throws IOException, ExecutionException, InterruptedException
    {
        DataInput dataEmpty = makeEmptyData();
        ScheduleExecutor executor = new ScheduleExecutor(1, 10, dataEmpty, apiBd, mockService);
        executor.runInteractive(new Scanner("\n"));

        verify(mockService, never()).schedule(any(Runnable.class), anyLong(), any());
    }

    @Test
    void runInteractiveScheduleTest() throws Exception
    {
        ScheduledFuture<?> mockFuture = mock(ScheduledFuture.class);

        CountDownLatch latch = new CountDownLatch(1);

        doAnswer(invocation -> {
            latch.countDown();
            return mockFuture;
        }).when(mockService).schedule(any(Runnable.class), anyLong(), any());
        doReturn(null).when(mockFuture).get();

        ScheduleExecutor executor = new ScheduleExecutor(1, 5, data, apiBd, mockService);
        executor.runInteractive(new Scanner("\n"));

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        verify(mockService, atLeastOnce()).schedule(any(Runnable.class), anyLong(), any());
        verify(mockFuture, atLeastOnce()).get();
    }

    private DataInput makeData()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "The Lord of the Rings Api",
                "The Lord of the Rings Api", "json", "3", "5", "4"};
        ArgsParser parser = new ArgsParser(args);
        parser.parseAll();
        return new DataInput(
                parser.apis,
                parser.format,
                DistributorMain.defaultSaves.get(parser.format),
                false,
                "",
                parser.threads,
                parser.period,
                parser.count);

    }
    private DataInput makeEmptyData()
    {
        String[] args = {"automatic", "Owen Wilson Wow Api", "The Lord of the Rings Api", "json", "3", "5", "4"};
        ArgsParser parser = new ArgsParser(args);
        parser.parseAll();
        return new DataInput(
                new ApiInfo[]{},
                parser.format,
                DistributorMain.defaultSaves.get(parser.format),
                false,
                "",
                parser.threads,
                parser.period,
                parser.count);

    }
}
