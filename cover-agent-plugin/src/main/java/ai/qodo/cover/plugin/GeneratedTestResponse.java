package ai.qodo.cover.plugin;

import java.util.List;

public record GeneratedTestResponse(String testingFramework, List<NewTest> newTests) {
}
