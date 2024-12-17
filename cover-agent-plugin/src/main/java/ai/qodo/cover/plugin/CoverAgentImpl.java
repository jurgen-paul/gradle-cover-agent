package ai.qodo.cover.plugin;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O;

import ai.qodo.cover.plugin.toml.PromptParser;
import ai.qodo.cover.plugin.toml.PromptParserImpl;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileTree;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;

public class CoverAgentImpl implements CoverAgent {
  private static final int MAX_TOKENS = 5000;
  private static final String PATH_SEPARATOR = System.getProperty("path.separator");
  private static final String JAVAC_COMMAND = "javac ";
  private final List<String> javaSourceDir = new ArrayList<>();
  private final List<File> javaSourceFiles = new ArrayList<>();
  private final List<File> javaTestSourceFiles = new ArrayList<>();
  private final String apiKey;
  private final String wanDBApiKey;
  private final int iterations;
  private final int coverage;
  private final String coverAgentBinaryPath;
  private final Project project;
  private final Logger logger;
  private final OpenAiChatModel.OpenAiChatModelBuilder openAiChatModelBuilder;
  private final List<TestingFramework> testingFrameworks = new ArrayList<>();
  private final DefaultTask task;
  private ModelPrompter modelPrompter;
  private Optional<String> javaClassPath = Optional.empty();
  private Optional<String> javaTestClassPath = Optional.empty();
  private String projectPath;
  private Optional<String> javaClassDir = Optional.empty();
  private String buildDirectory;
  private CoverAgentExecutor coverAgentExecutor;
  private Optional<String> javaCompileCommand = Optional.empty();
  private Optional<String> javaTestCompileCommand = Optional.empty();


  public CoverAgentImpl(CoverAgentBuilder builder) {
    this.apiKey = builder.getApiKey();
    this.wanDBApiKey = builder.getModel();
    this.iterations = builder.getIterations();
    this.coverage = builder.getCoverage();
    this.coverAgentBinaryPath = builder.getCoverAgentBinaryPath();
    this.modelPrompter = builder.getModelPrompter();
    this.javaClassPath = builder.getJavaClassPath();
    this.javaTestClassPath = builder.getJavaTestClassPath();
    this.projectPath = builder.getProjectPath();
    this.javaClassDir = builder.getJavaClassDir();
    this.buildDirectory = builder.getBuildDirectory();
    this.coverAgentExecutor = builder.getCoverAgentExecutor();
    this.project = builder.getProject();
    this.logger = project.getLogger();
    this.openAiChatModelBuilder = builder.openAiChatModelBuilder();
    this.task = builder.task();
  }

  public void init() {
    initModelPrompter();
    initExecutor();
    initDirectories();
    detectFramework();
    setJavaClassPath();
    setJavaTestClassPath();
    javaCompileCommand();
    testJavaCompileCommand();
  }

  public void detectFramework() {
    ConfigurationContainer container = project.getConfigurations();
    Configuration testImplementation = container.findByName("testImplementation");
    Configuration testCompile = container.findByName("testCompile");
    if (testImplementation != null) {
      detectFrameworkInConfiguration(testImplementation);
    } else if (testCompile != null) {
      detectFrameworkInConfiguration(testCompile);
    } else {
      this.logger.lifecycle("No test dependencies configuration found.");
    }
  }

  private void detectFrameworkInConfiguration(Configuration configuration) {
    configuration.getDependencies().forEach(dependency -> {
      String group = dependency.getGroup();
      String name = dependency.getName();
      String version = dependency.getVersion();
      if (group != null) {
        if (group.contains("org.junit.jupiter")) {
          testingFrameworks.add(new TestingFramework("Junit5", group, version, name));
        } else if (group.contains("org.junit.vintage")) {
          testingFrameworks.add(new TestingFramework("Junit4", group, version, name));
        } else if (group.contains("testng")) {
          testingFrameworks.add(new TestingFramework("TestNG", group, version, name));
        } else if (group.contains("spock")) {
          testingFrameworks.add(new TestingFramework("Spock Framework", group, version, name));
        }
      }
    });
  }

  private void setJavaTestClassPath() {
    JavaCompile javaTestCompileTask = project.getTasks().withType(JavaCompile.class).findByName("compileTestJava");
    if (javaTestCompileTask != null) {
      javaTestClassPath = java.util.Optional.of(buildClassPath(javaTestCompileTask));
      FileTree sourceFiles = javaTestCompileTask.getSource();
      Set<File> sourceDirs = sourceFiles.getFiles();
      javaTestSourceFiles.addAll(sourceDirs);
    }
  }

  private String buildClassPath(JavaCompile javaCompileTask) {
    StringBuilder builder = new StringBuilder();
    builder.append(javaCompileTask.getDestinationDirectory().get().getAsFile().getAbsolutePath()).append(":");
    for (File clFile : javaCompileTask.getClasspath().getFiles()) {
      builder.append(clFile.getAbsolutePath()).append(PATH_SEPARATOR);
      logger.debug("FILE {} - {}", javaCompileTask, clFile.getAbsolutePath());
    }
    logger.debug("{} path is {}", javaCompileTask, builder);
    return builder.toString();
  }

  private void setJavaClassPath() {
    JavaCompile javaCompileTask = project.getTasks().withType(JavaCompile.class).findByName("compileJava");
    if (javaCompileTask != null) {
      javaClassPath = java.util.Optional.of(buildClassPath(javaCompileTask));
      File classesDir = javaCompileTask.getDestinationDirectory().get().getAsFile();
      javaClassDir = java.util.Optional.of(classesDir.getAbsolutePath());
      FileTree sourceFiles = javaCompileTask.getSource();
      Set<File> sourceDirs = sourceFiles.getFiles();
      for (File srcDir : sourceDirs) {
        javaSourceDir.add(srcDir.getAbsolutePath());
        javaSourceFiles.add(srcDir);
      }
    }
  }

  private void initExecutor() {
    coverAgentExecutor =
        new CoverAgentExecutor.Builder().coverAgentBinaryPath(coverAgentBinaryPath).apiKey(apiKey).model(wanDBApiKey)
            .coverage(coverage).iterations(iterations).build();
  }

  private void initModelPrompter() {
    ChatLanguageModel model =
        openAiChatModelBuilder.apiKey(this.apiKey).modelName(GPT_4_O).maxTokens(MAX_TOKENS).build();
    PromptParser parser = new PromptParserImpl(logger);
    this.modelPrompter = new ModelPrompterImpl(logger, model, parser);
  }

  private void initDirectories() {
    Directory projectDirectory = project.getLayout().getProjectDirectory();
    projectPath = projectDirectory.getAsFile().getAbsolutePath();
    logger.debug("Root Project path {}", projectPath);
    File buildDir = project.getLayout().getBuildDirectory().getAsFile().get();
    if (!buildDir.exists()) {
      boolean created = buildDir.mkdirs();
      if (created) {
        logger.debug("Build directory created: {}", buildDir.getAbsolutePath());
      } else {
        logger.error("Failed to create build directory: {}", buildDir.getAbsolutePath());
      }
    } else {
      logger.debug("Build directory already exists: {}", buildDir.getAbsolutePath());
    }
    buildDirectory = buildDir.getAbsolutePath();
  }

  private void deleteFileIfExists(String filePath) {
    Path path = Paths.get(filePath);
    try {
      if (Files.exists(path)) {
        Files.delete(path);
        logger.info("Deleted file: {}", filePath);
      } else {
        logger.debug("File does not exist: {}", filePath);
      }
    } catch (IOException e) {
      logger.error("Failed to delete file: {}", filePath, e);
    }
  }

  private String convertListToString(List<String> list) {
    return String.join(PATH_SEPARATOR, list);
  }

  private List<String> findNeededJars(String mavenDependencyPath) throws CoverError {
    List<String> jarPaths = new ArrayList<>();
    try {
      ConfigurationContainer c = project.getConfigurations();
      DependencyHandler handler = project.getDependencies();
      Dependency dependency = handler.create(mavenDependencyPath);
      Configuration configuration = c.detachedConfiguration(dependency);
      Set<File> files = configuration.resolve();
      jarPaths.addAll(files.stream().map(File::getAbsolutePath).collect(Collectors.toList()));
      for (String jarPath : jarPaths) {
        logger.debug("Found jar path {}", jarPath);
      }
    } catch (Exception e) {
      logger.error("Failed to find needed jars {}", mavenDependencyPath, e);
      throw new CoverError("Failed to find " + mavenDependencyPath, e);
    }
    return jarPaths;
  }

  private String jacocoJavaReport(String reportPath, String execPath) throws CoverError {
    List<String> jars = findNeededJars("org.jacoco:org.jacoco.cli:0.8.12");
    String jarPath = convertListToString(jars);
    String sourcePath = convertListToString(javaSourceDir);
    String classFiles = "";
    if (javaClassDir.isPresent()) {
      classFiles = javaClassDir.get();
    }
    return "java -cp " + jarPath + " org.jacoco.cli.internal.Main report " + execPath + " --classfiles " + classFiles
        + " --sourcefiles " + sourcePath + " --csv " + reportPath;
  }

  private String javaAgentCommand(String jacocExecPath) throws CoverError {
    String standAloneJunit = findNeededJars("org.junit.platform:junit-platform-console-standalone:1.11.0").get(0);
    String jacocoAgent = findNeededJars("org.jacoco:org.jacoco.agent:0.8.11:runtime").get(0);
    String builder = "";
    if (javaTestClassPath.isPresent()) {
      builder = "java -javaagent:" + jacocoAgent + "=destfile=" + jacocExecPath + " -cp " + standAloneJunit + ":"
          + javaTestClassPath.get() + " org.junit.platform.console.ConsoleLauncher --scan-class-path ";
    } else {
      logger.error("Java test class path not found will not work to assign agent");
    }
    return builder;
  }

  public void javaCompileCommand() {
    JavaCompile javaCompileTask = project.getTasks().withType(JavaCompile.class).findByName("compileJava");
    StringBuilder builder = new StringBuilder();
    if (javaClassPath.isPresent()) {
      builder.append(JAVAC_COMMAND);
      CompileOptions options = javaCompileTask.getOptions();
      for (String arg : options.getAllCompilerArgs()) {
        builder.append(arg).append(" ");
      }
      builder.append("-d ").append(javaCompileTask.getDestinationDirectory().get().getAsFile().getAbsolutePath())
          .append(" ");
      builder.append("-classpath ");
      builder.append(javaClassPath.get());
      builder.append(" ");
      for (File sourceFile : javaCompileTask.getSource().getFiles()) {
        builder.append(sourceFile.getAbsolutePath()).append(" ");
      }
      javaCompileCommand = Optional.ofNullable(builder.toString());
    } else {
      logger.error("No Java class path provided");
    }
  }

  public void invoke() {
    logger.debug("Path to coverAgentBinaryPath {}", coverAgentBinaryPath);
    String jacocoReportPath = buildDirectory + "/jacocoTestReport.csv";
    File coverageReportFile = new File(jacocoReportPath);
    String jacocoExecPath = buildDirectory + "/test.exec";
    if (javaCompileCommand.isPresent()) {
      try {
        String javaAgentCommand = javaAgentCommand(jacocoExecPath);
        String jacocoJavaReportCommand = jacocoJavaReport(jacocoReportPath, jacocoExecPath);
        for (File pickedTestFile : javaTestSourceFiles) {
          logger.debug("First file to start adding coverage to is {}", pickedTestFile);
          try {
            deleteFileIfExists(jacocoReportPath);
            deleteFileIfExists(jacocoExecPath);
            TestInfoResponse testInfoResponse = modelPrompter.chatter(javaSourceFiles, pickedTestFile);
            String sourceFile = testInfoResponse.filepath();
            String testFile = pickedTestFile.getAbsolutePath();

            String jacocoCoverageCommand =
                String.format("%s;%s;%s;%s", javaCompileCommand.get(), javaTestCompileCommand.get(), javaAgentCommand,
                    jacocoJavaReportCommand);
            TestCreatorImpl testCreator =
                new TestCreatorImpl(sourceFile, testFile, project, jacocoCoverageCommand, projectPath, modelPrompter,
                    coverage, iterations, logger, coverageReportFile);
            testCreator.execute();
          } catch (CoverError e) {
            logger.error("Failed to find a matching Source file from list {} for test file {}, "
                + "moving on to other Test files in project.", javaSourceFiles, pickedTestFile, e);
          }
        }
      } catch (CoverError e) {
        logger.error("Failed to run coverAgent look a log file for more info ", e);
      }
    }
  }


  private void testJavaCompileCommand() {
    JavaCompile javaCompileTask = project.getTasks().withType(JavaCompile.class).findByName("compileTestJava");
    StringBuilder builder = new StringBuilder();
    if (javaTestClassPath.isPresent()) {
      builder.append(JAVAC_COMMAND);
      for (String arg : javaCompileTask.getOptions().getAllCompilerArgs()) {
        builder.append(arg).append(" ");
      }
      builder.append("-d ").append(javaCompileTask.getDestinationDirectory().get().getAsFile().getAbsolutePath())
          .append(" ");
      builder.append("-classpath ");
      builder.append(javaTestClassPath.get());
      builder.append(" ");
      for (File sourceFile : javaCompileTask.getSource().getFiles()) {
        builder.append(sourceFile.getAbsolutePath()).append(" ");
      }
      String compileCommand = builder.toString();
      javaTestCompileCommand = Optional.ofNullable(compileCommand);
      logger.debug("Executing javac command: {}", compileCommand);
    } else {
      logger.error("No JavaCompile task found!");
    }
  }

  public List<TestingFramework> getTestingFrameworks() {
    return testingFrameworks;
  }

}
