package ai.qodo.cover.plugin;

import dev.langchain4j.model.openai.OpenAiChatModel;
import java.util.Optional;
import org.gradle.api.Project;

public class CoverAgentBuilder {
  private ModelPrompter modelPrompter;
  private Optional<String> javaClassPath = Optional.empty();
  private Optional<String> javaTestClassPath = Optional.empty();
  private String projectPath;
  private Optional<String> javaClassDir = Optional.empty();
  private String buildDirectory;
  private Project project;

  public static CoverAgentBuilder builder() {
    return new CoverAgentBuilder();
  }

  public CoverAgentBuilder modelPrompter(ModelPrompter modelPrompter) {
    this.modelPrompter = modelPrompter;
    return this;
  }

  public CoverAgentBuilder javaClassPath(Optional<String> javaClassPath) {
    this.javaClassPath = javaClassPath;
    return this;
  }

  public CoverAgentBuilder javaTestClassPath(Optional<String> javaTestClassPath) {
    this.javaTestClassPath = javaTestClassPath;
    return this;
  }

  public CoverAgentBuilder projectPath(String projectPath) {
    this.projectPath = projectPath;
    return this;
  }

  public CoverAgentBuilder javaClassDir(Optional<String> javaClassDir) {
    this.javaClassDir = javaClassDir;
    return this;
  }

  public CoverAgentBuilder buildDirectory(String buildDirectory) {
    this.buildDirectory = buildDirectory;
    return this;
  }

  public CoverAgentBuilder project(Project project) {
    this.project = project;
    return this;
  }

  public CoverAgent build() {
    return new CoverAgent(this);
  }

  public ModelPrompter getModelPrompter() {
    return modelPrompter;
  }

  public Optional<String> getJavaClassPath() {
    return javaClassPath;
  }

  public Optional<String> getJavaTestClassPath() {
    return javaTestClassPath;
  }

  public String getProjectPath() {
    return projectPath;
  }

  public Optional<String> getJavaClassDir() {
    return javaClassDir;
  }

  public String getBuildDirectory() {
    return buildDirectory;
  }

  public Project getProject() {
    return project;
  }
}
