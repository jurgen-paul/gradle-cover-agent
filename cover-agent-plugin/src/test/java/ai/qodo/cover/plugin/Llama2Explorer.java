package ai.qodo.cover.plugin;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;
import org.slf4j.Marker;

public class Llama2Explorer {
  public static final Logger logger = new Logger() {

    public String replace(String msg, Object o) {
      if (o != null) {
        return msg.replace("{}", o.toString());
      } else {
        return msg;
      }
    }

    @Override
    public boolean isLifecycleEnabled() {
      return false;
    }

    @Override
    public void debug(String s, Object... objects) {

    }

    @Override
    public void lifecycle(String s) {

    }

    @Override
    public void lifecycle(String s, Object... objects) {

    }

    @Override
    public void lifecycle(String s, Throwable throwable) {

    }

    @Override
    public boolean isQuietEnabled() {
      return false;
    }

    @Override
    public void quiet(String s) {

    }

    @Override
    public void quiet(String s, Object... objects) {

    }

    @Override
    public void info(String s, Object... objects) {

    }

    @Override
    public void quiet(String s, Throwable throwable) {

    }

    @Override
    public boolean isEnabled(LogLevel logLevel) {
      return false;
    }

    @Override
    public void log(LogLevel logLevel, String s) {

    }

    @Override
    public void log(LogLevel logLevel, String s, Object... objects) {

    }

    @Override
    public void log(LogLevel logLevel, String s, Throwable throwable) {

    }

    @Override
    public String getName() {
      return "";
    }

    @Override
    public boolean isTraceEnabled() {
      return false;
    }

    @Override
    public void trace(String s) {

    }

    @Override
    public void trace(String s, Object o) {

    }

    @Override
    public void trace(String s, Object o, Object o1) {

    }

    @Override
    public void trace(String s, Object... objects) {

    }

    @Override
    public void trace(String s, Throwable throwable) {

    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
      return false;
    }

    @Override
    public void trace(Marker marker, String s) {

    }

    @Override
    public void trace(Marker marker, String s, Object o) {

    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {

    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isDebugEnabled() {
      return true;
    }

    @Override
    public void debug(String s) {
      System.out.println(s);
      System.out.println("-- --");
    }

    @Override
    public void debug(String s, Object o) {
      System.out.println(replace(s, o));
      System.out.println("-- --");
    }

    @Override
    public void debug(String s, Object o, Object o1) {

    }

    @Override
    public void debug(String s, Throwable throwable) {

    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
      return true;
    }

    @Override
    public void debug(Marker marker, String s) {

    }

    @Override
    public void debug(Marker marker, String s, Object o) {

    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {

    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isInfoEnabled() {
      return true;
    }

    @Override
    public void info(String s) {
      System.out.println(s);
      System.out.println("-- --");
    }

    @Override
    public void info(String s, Object o) {
      System.out.println(replace(s, o));
      System.out.println("-- --");
    }

    @Override
    public void info(String s, Object o, Object o1) {
      s = replace(s, o);
      s = replace(s, o1);
      System.out.println(s);
      System.out.println("-- --");
    }

    @Override
    public void info(String s, Throwable throwable) {

    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
      return true;
    }

    @Override
    public void info(Marker marker, String s) {

    }

    @Override
    public void info(Marker marker, String s, Object o) {

    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void info(Marker marker, String s, Object... objects) {

    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isWarnEnabled() {
      return false;
    }

    @Override
    public void warn(String s) {

    }

    @Override
    public void warn(String s, Object o) {

    }

    @Override
    public void warn(String s, Object... objects) {

    }

    @Override
    public void warn(String s, Object o, Object o1) {

    }

    @Override
    public void warn(String s, Throwable throwable) {

    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
      return false;
    }

    @Override
    public void warn(Marker marker, String s) {

    }

    @Override
    public void warn(Marker marker, String s, Object o) {

    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {

    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {

    }

    @Override
    public boolean isErrorEnabled() {
      return true;
    }

    @Override
    public void error(String s) {
      System.err.println(s);
      System.err.println("-- --");
    }

    @Override
    public void error(String s, Object o) {
      System.err.println(replace(s, o));
      System.err.println("-- --");
    }

    @Override
    public void error(String s, Object o, Object o1) {
      s = replace(s, o);
      s = replace(s, o1);
      System.err.println(s);
      System.err.println("-- --");
    }

    @Override
    public void error(String s, Object... objects) {
      for (Object o : objects) {
        s = replace(s, o);
      }
      System.err.println(s);
      System.err.println("-- --");

    }

    @Override
    public void error(String s, Throwable throwable) {
      System.err.println(replace(s, throwable.getMessage()));
      System.err.println("-- STACK --");
      throwable.printStackTrace();
      System.err.println("-- --");

    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
      return false;
    }

    @Override
    public void error(Marker marker, String s) {

    }

    @Override
    public void error(Marker marker, String s, Object o) {

    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {

    }

    @Override
    public void error(Marker marker, String s, Object... objects) {

    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
      s = replace(s, throwable.getMessage());
      System.err.println(s);
      System.err.println("-- STACK --");
      throwable.printStackTrace();
      System.err.println("-- --");
    }
  };
  private static final String SYSTEM_PROMPT = "You are a helpful AI assistant.";


  public static void main(String[] args) {

    ChatLanguageModel model =
        OllamaChatModel.builder().baseUrl("http://localhost:11434").modelName(ModelType.LLAMA2.getModelName())
            .format("json").timeout(Duration.ofMinutes(5)).temperature(0.2).build();

    Model gpt4Model = Model.builder().logger(logger).model(model).utility(new ModelUtility(logger))
        .modelType(ModelType.LLAMA2).build();

    try {
      chat(gpt4Model, new CoverAgentExecutor() {
        @Override
        public String execute(Project project, String sourceFile, String testFile, String jacocoReportPath,
                              String commandString, String projectPath) throws CoverError {
          return "SUCCESS";
        }
      });
    } catch (Exception e) {
      logger.error("Error occurred while communicating with Llama2: {}", e.getMessage());
      throw new RuntimeException("Failed to communicate with Llama2 model", e);
    }
  }

  public static void chat(Model model, CoverAgentExecutor executor) throws CoverError {
//    ModelPrompter prompter = new ModelPrompter(model, executor);
//    //spockframework
//    URL resourceUrl = Llama2Explorer.class.getClassLoader().getResource("Utility.java");
//    if (resourceUrl == null) {
//      logger.error("Could not find Calc.java in resources");
//      throw new RuntimeException("Calc.java resource file not found");
//    }
//    File calcFile = new File(resourceUrl.getFile());
//    TestFileResponse response = prompter.generateTestFile(calcFile, "spockframework");
//    System.out.println("TEST FILE IS " + response);
//    new ModelUtility(logger).createTestFile(response);

  }

}