package ai.qodo.cover.plugin;

import com.google.gson.Gson;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.output.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.api.logging.Logger;

public class ModelPrompter {
  private static final Gson GSON = new Gson();
  private static final String sourceFileMatchPrompt;

  static {
    try (InputStream inputStream = ModelPrompter.class.getResourceAsStream("/source_file_match_prompt.md")) {
      if (inputStream == null) {
        throw new IllegalStateException("Could not find source_file_match_prompt.md in resources");
      }
      sourceFileMatchPrompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to load source_file_match_prompt.md from resources", e);
    }
  }

  private final Logger logger;
  private final Model model;
  private final CoverAgentExecutor coverAgentExecutor;
  private final TestFileGenerator testFileGenerator;

  public ModelPrompter(Model model, CoverAgentExecutor coverAgentExecutor, TestFileGenerator testFileGenerator) {
    this.model = model;
    this.logger = model.getLogger();
    this.coverAgentExecutor = coverAgentExecutor;
    this.testFileGenerator = testFileGenerator;
  }

  public TestFileGenerator getTestFileGenerator() {
    return testFileGenerator;
  }

  public CoverAgentExecutor getCoverAgentExecutor() {
    return coverAgentExecutor;
  }

  public Model getModel() {
    return model;
  }

  public TestInfoResponse chatter(List<File> sourceFiles, File testFile) throws CoverError {
    try {
      List<String> absolutePaths = sourceFiles.stream().map(File::getAbsolutePath).collect(Collectors.toList());
      SourceFilePrompt prompt =
          new SourceFilePrompt(absolutePaths, testFile.getName(), model.getUtility().readFile(testFile));
      String userJson = GSON.toJson(prompt);
      logger.debug("User Prompt to Model: {}", userJson);
      ChatMessage systemChat = new SystemMessage(sourceFileMatchPrompt);
      ChatMessage userChat = new UserMessage(userJson);
      Response<AiMessage> message = model.getModel().generate(systemChat, userChat);
      logger.info("Model Response {} for TestFile {}", message.content().text(), testFile);
      String jsonString = model.getUtility().extractJson(message.content().text());
      logger.debug("Json extracted {}", jsonString);
      return GSON.fromJson(jsonString, TestInfoResponse.class);
    } catch (Exception e) {
      logger.error("A failure happened trying to get the source file that matches the provided test {}",
          e.getMessage());
      throw new CoverError("Huge error happened need to fix before proceeding ", e);
    }
  }

  @Override
  public String toString() {
    return "ModelPrompter{" + "model=" + model + ", coverAgentExecutor=" + coverAgentExecutor + ", testFileGenerator="
        + testFileGenerator + '}';
  }
}
