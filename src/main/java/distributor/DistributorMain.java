package distributor;

import distributor.apicollection.*;
import distributor.discrecord.DataInput;
import distributor.respproc.CsvProcessor;
import distributor.respproc.JsonProcessor;
import distributor.userparse.ArgsParser;
import distributor.userparse.CliParser;

import java.io.IOException;
import java.net.http.HttpTimeoutException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class DistributorMain
{
    public static String[] formats;
    public static ApiHandler[] apiNames;
    public static HashMap<String, ApiHandler> apiHandlers;
    public static HashMap<String, ResponseProcessor> formatsProc;
    public static HashMap<String, String> defaultSaves;
    public static final String saveJson = "src/main/resources/jsonSave.json";
    public static final String saveCsv = "src/main/resources/csvSave.csv";

    static {
        formats = new String[]{"json", "csv"};

        apiNames = new ApiHandler[]{
                new OneOfAllApi(),
                new OwenWilsonWowApi(),
                new SunsetTimeApi(),
                new NonResponseCatsApi(),
                new NonExistedApi()
        };
        apiHandlers = new HashMap<>();
        for (ApiHandler api : apiNames) {
            apiHandlers.put(api.getName(), api);
        }

        formatsProc = new HashMap<>();
        formatsProc.put("json", new JsonProcessor());
        formatsProc.put("csv", new CsvProcessor());

        defaultSaves = new HashMap<>();
        defaultSaves.put("json", saveJson);
        defaultSaves.put("csv", saveCsv);
    }

    public static void main(String[] args)
    {
        if (args.length == 0) {
            System.out.println("Error: enter working mode: automatic or interactive");
            return;
        }

        ApiBd apiBd = new ApiBd(apiHandlers, formatsProc);
        try (Scanner scanner = new Scanner(System.in)) {
            DataInput data = null;
            if (args[0].equals("automatic")) {
                data = automatic(args);
                ScheduleExecutor threadExecutor = new ScheduleExecutor(data.threads(), data.period(), data, apiBd);
                threadExecutor.runAutomatic(data.count());
            } else if (args[0].equals("interactive")) {
                data = interactive(scanner);
                ScheduleExecutor threadExecutor = new ScheduleExecutor(data.threads(), data.period(), data, apiBd);
                threadExecutor.runInteractive(scanner);
            } else {
                System.out.println("Incorrect mode");
                return;
            }

            apiBd.output(data.format(), data.path(), data.output());
        } catch (ExecutionException e) {
            System.out.println("<THREAD> " + e.getMessage());
        } catch (HttpTimeoutException e) {
            System.out.println("<CONNECTION> " + e.getMessage());
        } catch (IOException | InterruptedException | RuntimeException e) {
            System.out.println("<ERROR> " + e.getMessage());
        }
    }
    private static DataInput automatic(String[] args)
    {
        ArgsParser parser = new ArgsParser(args);
        parser.parseAll();
        return new DataInput(
                parser.apis,
                parser.format,
                defaultSaves.get(parser.format),
                false,
                "",
                parser.threads,
                parser.period,
                parser.count);
    }
    private static DataInput interactive(Scanner scanner)
    {
        CliParser parser = new CliParser(scanner);
        parser.parseAll();
        return new DataInput(
                parser.apis,
                parser.format,
                parser.path,
                parser.append,
                parser.output,
                parser.threads,
                parser.period,
                -1);
    }
}
