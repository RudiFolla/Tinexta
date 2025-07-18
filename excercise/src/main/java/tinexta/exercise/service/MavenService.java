package tinexta.exercise.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import tinexta.exercise.client.MavenClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MavenService {

    private final MavenClient mavenClient;
    private final ObjectMapper objectMapper;

    public MavenService(MavenClient mavenClient, ObjectMapper objectMapper) {
        this.mavenClient = mavenClient;
        this.objectMapper = objectMapper;
    }

    public JsonNode  search(String groupId, String artifactId) {
        JsonNode rootNode = mavenClient.search(groupId, artifactId);

        JsonNode docsNode = rootNode.path("response").path("docs");

        List<JsonNode> artifacts = new ArrayList<>();
        if (docsNode.isArray()) {
            docsNode.forEach(artifacts::add);
        } else {
            return objectMapper.createArrayNode();
        }

        artifacts.sort(Comparator.comparingLong((JsonNode node) -> {
            JsonNode timestampNode = node.get("timestamp");
            return (timestampNode != null && timestampNode.isNumber()) ? timestampNode.asLong() : 0L;
        }).reversed());
        return objectMapper.valueToTree(artifacts);
    }
}
