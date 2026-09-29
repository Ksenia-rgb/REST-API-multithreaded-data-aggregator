package distributor.httpadapt;

import distributor.WebAdapter;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpClientAdapter implements WebAdapter
{
    private final HttpClient client;

    public HttpClientAdapter()
    {
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }
    public HttpClientAdapter(HttpClient client)
    {
        this.client = client;
    }
    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
            throws IOException, InterruptedException
    {
        return client.send(request, responseBodyHandler);
    }
}
