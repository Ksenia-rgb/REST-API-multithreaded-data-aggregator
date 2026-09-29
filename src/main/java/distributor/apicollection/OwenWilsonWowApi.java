package distributor.apicollection;

import distributor.ApiHandler;
import distributor.WebAdapter;

public class OwenWilsonWowApi extends ApiHandler
{
    private static final String name;
    private static final String http;

    static
    {
        http = "https://owen-wilson-wow-api.onrender.com/wows/random/";
        name = "Owen Wilson Wow Api";
    }

    public OwenWilsonWowApi()
    {
        super();
    }
    public OwenWilsonWowApi(WebAdapter client)
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
}
