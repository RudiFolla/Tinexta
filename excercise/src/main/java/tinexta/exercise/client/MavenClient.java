package tinexta.exercise.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class MavenClient {
    private static final String MAVEN_SEARCH_API_URL = "https://search.maven.org/solrsearch/select";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MavenClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }
    public JsonNode search(String groupId, String artifactId) {
        try {
            String query = String.format("g:%s+AND+a:%s",
                    URLEncoder.encode(groupId, StandardCharsets.UTF_8),
                    URLEncoder.encode(artifactId, StandardCharsets.UTF_8));
            String url = String.format("%s?q=%s&core=gav&rows=100&wt=json", MAVEN_SEARCH_API_URL, query);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch data from Maven Search API. Status code: " + response.statusCode());
            }

            return objectMapper.readTree(response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error while calling Maven Search API", e);
        }
    }
}
