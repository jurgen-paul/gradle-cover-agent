package ai.qodo.cover.plugin;

import dev.langchain4j.model.openai.OpenAiChatModel;
import java.util.Optional;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;

public class CoverAgentBuilder {
  private String apiKey;
  private String model;
  private int iterations;
  private int coverage;
  private String coverAgentBinaryPath;
  private ModelPrompterImpl modelPrompterImpl;
  private Optional<String> javaClassPath = Optional.empty();
  private Optional<String> javaTestClassPath = Optional.empty();
  private String projectPath;
  private Optional<String> javaClassDir = Optional.empty();
  private String buildDirectory;
  private CoverAgentExecutor coverAgentExecutor;
  private Project project;
  private OpenAiChatModel.OpenAiChatModelBuilder openAiChatModelBuilder;
  private DefaultTask task;

  public static CoverAgentBuilder builder() {
    return new CoverAgentBuilder();
  }

  public CoverAgentBuilder apiKey(String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  public CoverAgentBuilder model(String model) {
    this.model = model;
    return this;
  }

  public CoverAgentBuilder iterations(int iterations) {
    this.iterations = iterations;
    return this;
  }

  public CoverAgentBuilder coverage(int coverage) {
    this.coverage = coverage;
    return this;
  }

  public CoverAgentBuilder coverAgentBinaryPath(String coverAgentBinaryPath) {
    this.coverAgentBinaryPath = coverAgentBinaryPath;
    return this;
  }

  public CoverAgentBuilder modelPrompter(ModelPrompterImpl modelPrompterImpl) {
    this.modelPrompterImpl = modelPrompterImpl;
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

  public CoverAgentBuilder coverAgentExecutor(CoverAgentExecutor coverAgentExecutor) {
    this.coverAgentExecutor = coverAgentExecutor;
    return this;
  }

  public CoverAgentBuilder project(Project project) {
    this.project = project;
    return this;
  }

  public CoverAgentBuilder openAiChatModelBuilder(OpenAiChatModel.OpenAiChatModelBuilder openAiChatModelBuilder) {
    this.openAiChatModelBuilder = openAiChatModelBuilder;
    return this;
  }

  public CoverAgentBuilder task(DefaultTask task) {
    this.task = task;
    return this;
  }


  public CoverAgent build() {
    return new CoverAgentImpl(this);
  }

  public CoverAgent buildVersion2() {
    return new CoverAgentV2Impl(this);
  }

  public OpenAiChatModel.OpenAiChatModelBuilder openAiChatModelBuilder() {
    return openAiChatModelBuilder;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getModel() {
    return model;
  }

  public int getIterations() {
    return iterations;
  }

  public int getCoverage() {
    return coverage;
  }

  public String getCoverAgentBinaryPath() {
    return coverAgentBinaryPath;
  }

  public ModelPrompter getModelPrompter() {
    return modelPrompterImpl;
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

  public CoverAgentExecutor getCoverAgentExecutor() {
    return coverAgentExecutor;
  }

  public Project getProject() {
    return project;
  }

  public DefaultTask task() {
    return task;
  }
}
