package distributor.apicollection;

import distributor.ApiHandler;
import distributor.WebAdapter;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

public class OneOfAllApi extends ApiHandler
{
    private static final String name;
    private static final String http;
    private final static String authKey;

    static
    {
        http = "https://the-one-api.dev/v2/character";
        name = "The Lord of the Rings Api";
        authKey = Dotenv.load().get("LOTR_KEY");
    }

    public OneOfAllApi()
    {
        super();
    }
    public OneOfAllApi(WebAdapter client)
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
    protected HttpRequest makeRequest(String address)
    {
        return HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(address))
                .header("Authorization", "Bearer " + authKey)
                .build();
    }
    @Override
    protected String addQuery(String address, String[] query)
    {
        StringBuilder addressBuilder = new StringBuilder(address);
        if (query == null || query.length == 0) {
            addressBuilder.append("?name=").append("Frodo%20Baggins");
        } else {
            for (String str : query) {
                addressBuilder.append("?").append(str);
            }
        }
        return addressBuilder.toString();
    }
}
