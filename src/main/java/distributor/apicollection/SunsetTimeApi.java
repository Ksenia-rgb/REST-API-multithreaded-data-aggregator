package distributor.apicollection;

import distributor.ApiHandler;
import distributor.WebAdapter;

public class SunsetTimeApi extends ApiHandler
{
    private static final String name;
    private static final String http;

    static
    {
        http = "https://api.sunrise-sunset.org/json";
        name = "Sunset Sunrise Time Api";
    }

    public SunsetTimeApi()
    {
        super();
    }
    public SunsetTimeApi(WebAdapter client)
    {
        super(client);
    }

    @Override
    public String getName()
    {
        return name;
    }
    @Override
    public String getHttp()
    {
        return http;
    }
    @Override
    protected String addQuery(String address, String[] query)
    {
        StringBuilder addressBuilder = new StringBuilder(address);
        if (query == null || query.length == 0) {
            addressBuilder.append("?lat=60.007420&lng=30.372698");
        } else {
            addressBuilder.append("?").append(query[0]);
            for (int i = 1; i < query.length; i++) {
                addressBuilder.append("&").append(query[i]);
            }
        }
        return addressBuilder.toString();
    }
}
