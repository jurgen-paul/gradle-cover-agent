package ai.qodo.cover.plugin;

import org.gradle.api.Project;

public interface CoverAgentExecutor {
  String execute(Project project, String sourceFile, String testFile, String jacocoReportPath,
                        String commandString, String projectPath) throws CoverError;
}
