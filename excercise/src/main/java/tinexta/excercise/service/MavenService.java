package tinexta.excercise.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import tinexta.excercise.client.MavenClient;

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

        // 2. Navigate to the array of artifacts ("docs")
        JsonNode docsNode = rootNode.path("response").path("docs");

        // 3. Convert the JSON array into a standard Java List for sorting
        List<JsonNode> artifacts = new ArrayList<>();
        if (docsNode.isArray()) {
            docsNode.forEach(artifacts::add);
        } else {
            // If docs doesn't exist or isn't an array, return an empty JSON array
            return objectMapper.createArrayNode();
        }

        // 4. Sort the list by the "timestamp" field in descending order
        artifacts.sort(Comparator.comparingLong((JsonNode node) -> {
            JsonNode timestampNode = node.get("timestamp");
            return (timestampNode != null && timestampNode.isNumber()) ? timestampNode.asLong() : 0L;
        }).reversed());
        // 5. Convert the sorted List back into a JsonNode (specifically an ArrayNode) and return it
        return objectMapper.valueToTree(artifacts);
    }
}
