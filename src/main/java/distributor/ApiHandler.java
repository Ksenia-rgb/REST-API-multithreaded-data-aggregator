package distributor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import distributor.discrecord.Note;
import distributor.httpadapt.HttpClientAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class ApiHandler
{
    private final WebAdapter client;

    abstract public String getName();
    abstract public String getHttp();

    public ApiHandler()
    {
        this.client = new HttpClientAdapter();
    }
    public ApiHandler(WebAdapter client)
    {
        this.client = client;
    }

    public final Note send(String[] query) throws IOException, InterruptedException
    {
        String address = addQuery(getHttp(), query);
        HttpRequest request = makeRequest(address);

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            if (e.getMessage() == null) {
                throw new IOException("The network connection to the website cannot be established");
            }
            throw e;
        }
        if (response.body() == null || response.body().isEmpty()) {
            throw new IOException("The website cannot return correct response");
        }

        return new Note(UUID.randomUUID().getMostSignificantBits(),
                getName(), LocalDateTime.now().toString(), parseResponseJson(response.body()));

    }

    protected String addQuery(String address, String[] query)
    {
        StringBuilder addressBuilder = new StringBuilder(address);
        if (query != null) {
            for (String str : query) {
                addressBuilder.append("?").append(str);
            }
        }
        return addressBuilder.toString();
    }
    protected HttpRequest makeRequest(String address)
    {
        return HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(address))
                .build();
    }

    private JsonNode parseResponseJson(String response) throws JsonProcessingException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(response);
    }
}

//сайт не отвечает долго: IOException(Connection reset)

//несуществующий URL
// 1) Если не установлен request timeout - IOException(null)
// 2) Если установлен - смотря что раньше: IOException(null) или HttpTimeoutConnection(Http connect timed out)
//timeout 10 - timed out
//timeout 15 - IOException

//нет wi-fi - IOException(null)

