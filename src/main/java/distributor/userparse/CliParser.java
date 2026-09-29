package distributor.userparse;

import distributor.discrecord.ApiInfo;
import distributor.UserParser;

import java.util.ArrayList;
import java.util.Scanner;

import static distributor.DistributorMain.*;

public class CliParser implements UserParser
{
    public ApiInfo[] apis;
    public String format, path;
    public boolean append;
    public String output;
    public int threads;
    public int period;

    private final Scanner scanner;

    public CliParser(Scanner scanner)
    {
        this.scanner = scanner;
    }

    @Override
    public void parseAll()
    {
        parseNames();
        parseFormat();
        parsePath();
        parseAppend();
        parseOutput();
        parseThreads();
        parsePeriod();
    }
    @Override
    public void parseNames()
    {
        System.out.println("Choose api for request, enter every api with query on new line:");
        for (int i = 0; i < apiNames.length; i++) {
            System.out.println(i + 1 + ": " + apiNames[i].getName());
        }
        System.out.println(apiNames.length + 1 + ": End");

        ArrayList<ApiInfo> apisRead = new ArrayList<>();
        while (scanner.hasNextLine()) {
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("Incorrect API index, try again");
                continue;
            }
            int index = scanner.nextInt();
            String queryStr = scanner.nextLine().trim();
            if (index < 1 || index > apiNames.length + 1) {
                System.out.println("Incorrect API index, try again");
                continue;
            }
            if (index == apiNames.length + 1) {
                break;
            }
            String name = apiNames[index - 1].getName();
            String[] query = new String[]{};
            if (!queryStr.isEmpty()) {
                query = queryStr.split(" ");
            }
            apisRead.addLast(new ApiInfo(name, query));
        }
        apis = apisRead.toArray(ApiInfo[]::new);
    }
    @Override
    public void parseFormat()
    {
        System.out.println("Enter save format:");
        System.out.println("1: json");
        System.out.println("2: csv");
        while (scanner.hasNextLine()) {
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("Incorrect format, try again");
                continue;
            }
            int mode = scanner.nextInt();
            scanner.nextLine();
            if (mode < 1 || mode > formats.length) {
                System.out.println("Incorrect format, try again");
                continue;
            }
            format = formats[mode - 1];
            break;
        }
    }
    public void parsePath()
    {
        System.out.println("Enter file path or choose default:");
        System.out.println("1: default for " + format + ": " + defaultSaves.get(format));
        String entered = scanner.nextLine();
        if (entered.equals("1")) {
            path = defaultSaves.get(format);
        } else {
            path = entered;
        }
    }
    public void parseAppend()
    {
        System.out.println("Enter file operating mode:");
        System.out.println("1: append to existing file");
        System.out.println("2: rewrite or create new file");
        while (scanner.hasNextLine()) {
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("Incorrect append value, try again");
                continue;
            }
            int mode = scanner.nextInt();
            scanner.nextLine();
            if (mode != 1 && mode != 2) {
                System.out.println("Incorrect append value, try again");
                continue;
            }
            append = (mode == 1);
            break;
        }
    }
    public void parseOutput()
    {
        System.out.println("Enter console output mode:");
        System.out.println("0: Not output");
        for (int i = 0; i < apiNames.length; i++) {
            System.out.println(i + 1 + ": " + apiNames[i].getName());
        }
        System.out.println(apiNames.length + 1 + ": Output all");

        while (scanner.hasNextLine()) {
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("Incorrect output value, try again");
                continue;
            }
            int index = scanner.nextInt();
            scanner.nextLine();
            if (index < 0 || index > apiNames.length + 1) {
                System.out.println("Incorrect output value, try again");
                continue;
            }
            if (index == 0) {
                output = "";
            } else if (index == apiNames.length + 1) {
                output = "all";
            } else {
                output = apiNames[index - 1].getName();
            }
            break;
        }
    }
    @Override
    public void parseThreads()
    {
        System.out.println("Enter threads count:");
        while (scanner.hasNextLine()) {
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("Incorrect threads value, try again");
                continue;
            }
            int threadsTemp = scanner.nextInt();
            scanner.nextLine();
            if (threadsTemp < 0) {
                System.out.println("Incorrect threads value, try again");
                continue;
            }
            threads = threadsTemp;
            break;
        }
    }
    @Override
    public void parsePeriod()
    {
        System.out.println("Enter execute period:");
        while (scanner.hasNextLine()) {
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("Incorrect period value, try again");
                continue;
            }
            int periodTemp = scanner.nextInt();
            scanner.nextLine();
            if (periodTemp < 0) {
                System.out.println("Incorrect period value, try again");
                continue;
            }
            period = periodTemp;
            break;
        }
    }
}
