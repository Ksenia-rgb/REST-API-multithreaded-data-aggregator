package distributor.httpadapt;

import distributor.apicollection.OwenWilsonWowApi;
import distributor.httpadapt.HttpClientAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class HttpClientAdapterTest
{
    @Test
    void sendThrowIOExceptionNullMessageTest() throws IOException, InterruptedException
    {
        OwenWilsonWowApi api = new OwenWilsonWowApi();

        HttpClient mockClient = Mockito.mock(HttpClient.class);
        HttpResponse<String> mockResponse = Mockito.mock(HttpResponse.class);
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(api.getHttp()))
                .build();


        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        HttpClientAdapter adapter = new HttpClientAdapter(mockClient);

        assertDoesNotThrow(() -> adapter.send(request, HttpResponse.BodyHandlers.ofString()));
    }
}
