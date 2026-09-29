package distributor.apicollection;

import distributor.ApiHandler;
import distributor.WebAdapter;

public class NonResponseCatsApi extends ApiHandler
{
    private static final String name;
    private static final String http;

    static
    {
        http = "https://http.cat/100";
        name = "Non Response Cats Api";
    }

    public NonResponseCatsApi()
    {
        super();
    }
    public NonResponseCatsApi(WebAdapter client)
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
