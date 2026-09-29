package distributor;

import distributor.discrecord.ApiInfo;
import distributor.discrecord.DataInput;
import distributor.discrecord.Note;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class ScheduleExecutor
{
    private final ScheduledExecutorService service;
    private final int period;

    private final AtomicBoolean running;
    private final ArrayList<ScheduledFuture<?>> futures;
    private final ReentrantLock consoleLock;

    private final DataInput data;
    private final ApiBd apiBd;

    public ScheduleExecutor(int n, int period, DataInput data, ApiBd apiBd)
    {
        this(n, period, data, apiBd, Executors.newScheduledThreadPool(n));
    }

    public ScheduleExecutor(int n, int period, DataInput data, ApiBd apiBd, ScheduledExecutorService service)
    {
        if (period < 0) {
            throw new IllegalArgumentException("Incorrect period value");
        }
        this.service = service;
        this.period = period;
        this.apiBd = apiBd;
        this.data = data;
        this.running = new AtomicBoolean(true);
        this.futures = new ArrayList<>();
        this.consoleLock = new ReentrantLock();
    }

    public void runAutomatic(int maxCount) throws IOException, InterruptedException, ExecutionException
    {
        if (data.apis().length == 0 || maxCount == 0) {
            return;
        }

        ArrayList<ArrayList<ApiInfo>> batches = formBatches(false);

        Future<?> future1 = service.schedule(new TaskRunnable(batches.getFirst(), data), 0, TimeUnit.SECONDS);
        future1.get();
        scheduleBatches(batches, 1, 0);
        waitAndClear();

        for (int count = 1; count < maxCount; count++) {
            scheduleBatches(batches, 0, 0);
            waitAndClear();
        }
        waitAndClear();
        System.out.println("Max count iterations done");
        running.set(false);
        complete();
        apiBd.output(data.format(), data.path(), data.output());
    }
    public void runInteractive(Scanner scanner) throws IOException, InterruptedException, ExecutionException
    {
        if (data.apis().length == 0) {
            return;
        }

        Thread inputThread = new Thread(() -> {
            scanner.nextLine();
            System.out.println("Stop command entered");
            running.set(false);
        });
        inputThread.start();

        ArrayList<ArrayList<ApiInfo>> batches = formBatches(true);

        Future<?> future1 = service.schedule(new TaskRunnable(batches.getFirst(), data), 0, TimeUnit.SECONDS);
        future1.get();
        while (running.get()) {
            scheduleBatches(batches, 0, 0);
            waitAndClear();
        }
        waitAndClear();
        complete();
        apiBd.output(data.format(), data.path(), data.output());
    }
    private void complete()
    {
        service.shutdown();

        try {
            if (!service.awaitTermination(30, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
        }
    }

    private void scheduleBatches(ArrayList<ArrayList<ApiInfo>> batches, int indexBegin, int delay)
    {
        for (int i = indexBegin; i < batches.size(); i++) {
            futures.add(service.schedule(
                    new TaskRunnable(batches.get(i), data.withAppend(true)),
                    delay, TimeUnit.SECONDS)
            );
        }
    }

    private void waitAndClear() throws ExecutionException, InterruptedException
    {
        for (Future<?> future: futures) {
            if (!future.isDone()) {
                future.get();
            }
        }
        futures.clear();
    }

    private ArrayList<ArrayList<ApiInfo>> formBatches(boolean balance)
    {
        HashMap<String, ArrayList<ApiInfo>> batches = new HashMap<>();
        for (ApiInfo apiInfo: data.apis()) {
            if (batches.containsKey(apiInfo.name())) {
                batches.get(apiInfo.name()).add(apiInfo);
            } else {
                batches.put(apiInfo.name(), new ArrayList<>(List.of(apiInfo)));
            }
        }
        if (balance) {
            int maxLength = 0;
            for (Map.Entry<String, ArrayList<ApiInfo>> entry: batches.entrySet()) {
                maxLength = Math.max(maxLength, entry.getValue().size());
            }

            for (Map.Entry<String, ArrayList<ApiInfo>> entry: batches.entrySet()) {
                ArrayList<ApiInfo> infos = entry.getValue();
                int index = 0;
                while (!infos.isEmpty() && infos.size() < maxLength) {
                    infos.add(infos.get(index));
                    index = (index + 1) % infos.size();
                }
                entry.setValue(infos);
            }
        }
        return new ArrayList<>(batches.values());
    }


    class TaskRunnable implements Runnable
    {
        private final ArrayList<ApiInfo> batch;
        private final DataInput data;

        TaskRunnable(ArrayList<ApiInfo> batch, DataInput data)
        {
            this.batch = batch;
            this.data = data;
        }
        @Override
        public void run()
        {
            if (!running.get()) {
                return;
            }
            int diff = 0;
            int index = 0;
            boolean appendReal = this.data.append();
            for (ApiInfo apiInfo: batch)
            {
                if (index != 0) {
                    appendReal = true;
                }
                try {
                    Note note = apiBd.executeSingle(apiInfo);
                    LocalDateTime timeBegin = LocalDateTime.now();
                    apiBd.saveSingle(note, this.data.format(), this.data.path(), appendReal);
                    diff = LocalDateTime.now().getSecond() - timeBegin.getSecond();
                    index++;

                    consoleLock.lock();
                    System.out.println("Batch: " + apiInfo.name() + " Index: " + index + " Time: " + LocalDateTime.now());
                    consoleLock.unlock();
                } catch (IOException | InterruptedException e) {
                    consoleLock.lock();
                    System.out.println("<ERROR> " + e.getMessage());
                    consoleLock.unlock();
                } finally {
                    try {
                        Thread.sleep(Duration.ofSeconds(period - diff));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}
