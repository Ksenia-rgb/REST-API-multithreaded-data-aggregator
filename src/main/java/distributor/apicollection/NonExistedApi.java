package distributor.apicollection;

import distributor.ApiHandler;
import distributor.WebAdapter;

public class NonExistedApi extends ApiHandler
{
    private static final String name;
    private static final String http;

    static
    {
        http = "https://non-existed-api";
        name = "Non Existed Api";
    }

    public NonExistedApi()
    {
        super();
    }
    public NonExistedApi(WebAdapter client)
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
