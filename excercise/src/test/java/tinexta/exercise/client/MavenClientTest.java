package tinexta.exercise.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class MavenClientTest {

    private HttpClient httpClient;
    private MavenClient mavenClient;

    @BeforeEach
    void setUp() {
        httpClient = mock(HttpClient.class);
        ObjectMapper objectMapper = new ObjectMapper();
        mavenClient = new MavenClient(httpClient, objectMapper);
    }

    @Test
    void fetchArtifactData_success() throws Exception {
        // Mock HTTP response
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        String jsonResponse = """
            {
              "response": {
                "docs": [
                  {"id": "1", "timestamp": 1650000000000}
                ]
              }
            }
            """;

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        JsonNode result = mavenClient.search("com.example", "my-artifact");

        assertNotNull(result);
        assertTrue(result.has("response"));
        assertTrue(result.path("response").has("docs"));
        assertEquals(1, result.path("response").path("docs").size());
        assertEquals("1", result.path("response").path("docs").get(0).path("id").asText());
    }

    @Test
    void fetchArtifactData_non200Status_throws() throws Exception {
        HttpResponse<String> httpResponse = mock(HttpResponse.class);

        when(httpResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                mavenClient.search("com.example", "my-artifact")
        );

        assertTrue(ex.getMessage().contains("Failed to fetch data"));
    }

    @Test
    void fetchArtifactData_ioException_throws() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new IOException("IO failure"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                mavenClient.search("com.example", "my-artifact")
        );

        assertTrue(ex.getMessage().contains("Error while calling Maven Search API"));
    }
}

