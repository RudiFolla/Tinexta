package tinexta.exercise.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tinexta.exercise.service.MavenService;

@RestController
@RequestMapping("/api/v1")
public class MavenController {
    private final MavenService mavenService;

    public MavenController(MavenService mavenService) {
        this.mavenService = mavenService;
    }

    @GetMapping("/search")
    public JsonNode search(@RequestParam String groupId, @RequestParam String artifactId) {
        return mavenService.search(groupId, artifactId);
    }
}
