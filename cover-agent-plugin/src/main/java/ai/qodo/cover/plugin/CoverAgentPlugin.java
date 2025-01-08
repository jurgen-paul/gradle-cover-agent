package ai.qodo.cover.plugin;


import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.io.File;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public class CoverAgentPlugin implements Plugin<Project> {
  static final Integer DEFAULT_ITERATIONS = Integer.valueOf(1);
  static final Integer DEFAULT_PERCENTAGE = Integer.valueOf(75);
  static final Duration TIMEOUT_FOR_LLAMA2 = Duration.ofMinutes(5);
  static final double LLAMA2_TEMPERATURE = 0.0;

  @Override
  public void apply(Project project) {
    Logger logger = project.getLogger();
    logger.info("Running plugin version {}", "0.0.3");
    project.getExtensions().create("coverAgent", CoverAgentExtension.class, project);

    project.afterEvaluate(p -> {
      logger.debug("Project evaluation in progress - deferring CoverAgent task registration until completion");
      Optional<Map<String, File>> testFileDirectory = findTestDirectory(project, logger);

      CoverAgentExtension extension = p.getExtensions().findByType(CoverAgentExtension.class);
      if (extension != null && testFileDirectory.isPresent()) {
        p.getTasks().register("coverAgentTask", CoverAgentTask.class, task -> {
          task.setModelPrompter(createModelPrompter(extension, logger, testFileDirectory.get()));
          task.setGroup("verification");
          task.setDescription("Runs the cover agent task attempting to increase code coverage");
          task.getOutputs().upToDateWhen(t -> false);
        });
      }
    });
  }

  private Optional<Map<String, File>> findTestDirectory(Project project, Logger logger) {
    SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
    Optional<Map<String, File>> testDirectories = Optional.empty();
    for (SourceSet sourceSet : sourceSets) {
      if (sourceSet.getName().equals("test")) {
        FileCollection testFiles = sourceSet.getAllJava().getSourceDirectories();
        Set<File> testSourceBase = testFiles.getFiles();
        testDirectories = Optional.of(
            testSourceBase.stream().peek(f -> logger.debug("Processing test directory path: {}", f.getAbsolutePath()))
                .collect(Collectors.toMap(f -> f.getName(), f -> f)));
      }
    }
    return testDirectories;
  }

  private ModelPrompter createModelPrompter(CoverAgentExtension extension, Logger logger,
                                            Map<String, File> testFileDirectories) {
    final String apiKey = extension.getApiKey().getOrNull();
    final String baseUrl = extension.getModelBaseUrl().getOrNull();
    final String modelKey = extension.getModel().getOrElse(ModelType.GPT_4.getModelName());
    final String coverAgentBinaryPath = extension.getCoverAgentBinaryPath().getOrNull();
    final int iterations = extension.getIterations().getOrElse(DEFAULT_ITERATIONS);
    final int coverage = extension.getCoverage().getOrElse(DEFAULT_PERCENTAGE);
    if (coverAgentBinaryPath == null) {
      throw new CoverRuntimeError("coverAgentBinaryPath is required!");
    }
    CoverAgentExecutorImpl.Builder executerBuilder =
        CoverAgentExecutorImpl.builder().coverAgentBinaryPath(coverAgentBinaryPath).coverage(coverage)
            .iterations(iterations);

    TestFileGenerator testFileGenerator = new TestFileGeneratorImpl(testFileDirectories, logger);


    switch (ModelType.fromModelName(modelKey)) {
      case GPT_4:
        if (apiKey == null) {
          throw new CoverRuntimeError("OpenAI API key is required for OpenAI model type and its null");
        }
        logger.debug("Creating OpenAI chat model");
        ChatLanguageModel gpt4ChatModel =
            OpenAiChatModel.builder().apiKey(extension.getApiKey().get()).modelName(ModelType.GPT_4.getModelName())
                .build();
        Model gpt4Model = Model.builder().logger(logger).model(gpt4ChatModel).utility(new ModelUtility(logger))
            .modelType(ModelType.GPT_4).baseUrl(baseUrl).build();

        return new ModelPrompter(gpt4Model, executerBuilder.model(gpt4Model).build(), testFileGenerator);
      case LLAMA2:
        if (baseUrl == null) {
          throw new CoverRuntimeError("Base path is required for Llama2 model type");
        }
        logger.debug("Creating Llama2 local model");
        ChatLanguageModel llama2ChatModel =
            OllamaChatModel.builder().baseUrl(baseUrl).modelName(ModelType.LLAMA2.getModelName()).format("json")
                .timeout(TIMEOUT_FOR_LLAMA2).temperature(LLAMA2_TEMPERATURE).build();

        Model llama2Model = Model.builder().logger(logger).model(llama2ChatModel).utility(new ModelUtility(logger))
            .modelType(ModelType.LLAMA2).baseUrl(baseUrl).build();

        return new ModelPrompter(llama2Model, executerBuilder.model(llama2Model).build(), testFileGenerator);

      default:
        throw new CoverRuntimeError("Unsupported model type: " + extension.getModel().getOrNull());
    }
  }
}
