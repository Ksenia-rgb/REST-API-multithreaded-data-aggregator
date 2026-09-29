package distributor.userparse;

import distributor.discrecord.ApiInfo;
import distributor.UserParser;

import java.util.ArrayList;

import static distributor.DistributorMain.*;

public class ArgsParser implements UserParser
{
    public ApiInfo[] apis;
    public String format;
    public int threads;
    public int period;
    public int count;
    private final String[] args;

    public ArgsParser(String[] args)
    {
        if (args.length < 4)
        {
            throw new IllegalArgumentException("Too few arguments");
        }
        this.args = args;
    }
    public void parseAll()
    {
        parseNames();
        parseFormat();
        parseThreads();
        parsePeriod();
        parseCount();
    }
    public void parseNames()
    {
        ArrayList<ApiInfo> apisRead = new ArrayList<>();
        for (int i = 1; i < args.length - 4; i++) {
            String name = args[i];
            if (!isCorrectApiName(name)) {
                throw new IllegalArgumentException("Incorrect argument api names: api \"" + name + "\" is unknown");
            }
            apisRead.addLast(new ApiInfo(name, new String[]{}));
        }
        apis = apisRead.toArray(ApiInfo[]::new);
    }
    public void parseFormat()
    {
        format = args[args.length - 4];
        for (String f : defaultSaves.keySet()) {
            if (f.equals(format)) {
                return;
            }
        }
        throw new IllegalArgumentException("Incorrect argument: format");
    }
    public void parseThreads()
    {
        try {
            threads = Integer.parseInt(args[args.length - 3]);
            if (threads <= 0) {
                throw new IllegalArgumentException("Incorrect argument: threads");
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Incorrect argument: threads");
        }
    }
    public void parsePeriod()
    {
        try {
            period = Integer.parseInt(args[args.length - 2]);
            if (period < 0) {
                throw new IllegalArgumentException("Incorrect argument: period");
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Incorrect argument: period");
        }
    }
    public void parseCount()
    {
        try {
            count = Integer.parseInt(args[args.length - 1]);
            if (count < 0) {
                throw new IllegalArgumentException("Incorrect argument: count");
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Incorrect argument: count");
        }
    }
    private boolean isCorrectApiName(String name)
    {
        return apiHandlers.containsKey(name);
    }
}
