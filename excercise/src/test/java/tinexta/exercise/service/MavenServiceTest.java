package tinexta.exercise.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tinexta.exercise.client.MavenClient;

public class MavenServiceTest {

    private MavenClient mavenClient;
    private ObjectMapper objectMapper;
    private MavenService mavenService;

    @BeforeEach
    void setUp() {
        mavenClient = mock(MavenClient.class);
        objectMapper = new ObjectMapper();
        mavenService = new MavenService(mavenClient, objectMapper);
    }

    @Test
    void search_sortsArtifactsByTimestampDescending() {
        String jsonResponse = """
        {
          "response": {
            "docs": [
              {"id": "1", "timestamp": 1650000000000},
              {"id": "2", "timestamp": 1700000000000},
              {"id": "3", "timestamp": 1600000000000}
            ]
          }
        }
        """;

        when(mavenClient.search(anyString(), anyString())).thenReturn(parse(jsonResponse));

        JsonNode result = mavenService.search("com.example", "artifact");

        assertTrue(result.isArray());
        assertEquals(3, result.size());

        // Check if sorted descending by timestamp: id=2, id=1, id=3
        assertEquals("2", result.get(0).path("id").asText());
        assertEquals("1", result.get(1).path("id").asText());
        assertEquals("3", result.get(2).path("id").asText());
    }

    @Test
    void search_handlesMissingDocs_returnsEmptyArray() {
        String jsonResponse = """
        {
          "response": {}
        }
        """;

        when(mavenClient.search(anyString(), anyString())).thenReturn(parse(jsonResponse));

        JsonNode result = mavenService.search("com.example", "artifact");

        assertTrue(result.isArray());
        assertEquals(0, result.size());
    }

    @Test
    void search_handlesDocsNotArray_returnsEmptyArray() {
        String jsonResponse = """
        {
          "response": {
            "docs": {}
          }
        }
        """;

        when(mavenClient.search(anyString(), anyString())).thenReturn(parse(jsonResponse));

        JsonNode result = mavenService.search("com.example", "artifact");

        assertTrue(result.isArray());
        assertEquals(0, result.size());
    }

    private JsonNode parse(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

