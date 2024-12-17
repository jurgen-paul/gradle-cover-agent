package ai.qodo.cover.plugin;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O;

import ai.qodo.cover.plugin.toml.PromptParser;
import ai.qodo.cover.plugin.toml.PromptParserImpl;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.Directory;
import org.gradle.api.file.FileTree;
import org.gradle.api.internal.TaskInternal;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.testing.jacoco.tasks.JacocoReport;

public class CoverAgentV2Impl implements CoverAgent {
  private static final int PERCENTAGE_CONVERT = 100;
  private static final int MAX_TOKENS = 5000;
  private static final String PATH_SEPARATOR = System.getProperty("path.separator");
  private final String apiKey;
  private final String model;
  private final int iterations;
  private final int coverage;
  private final Project project;
  private final Logger logger;
  private final OpenAiChatModel.OpenAiChatModelBuilder openAiChatModelBuilder;
  private final List<TestingFramework> testingFrameworks = new ArrayList<>();
  private final String buildDirectory;
  private final DefaultTask task;
  private ModelPrompter modelPrompter;
  private String projectPath;


  public CoverAgentV2Impl(CoverAgentBuilder builder) {
    this.apiKey = builder.getApiKey();
    this.model = builder.getModel();
    this.iterations = builder.getIterations();
    this.coverage = builder.getCoverage();
    this.modelPrompter = builder.getModelPrompter();
    this.projectPath = builder.getProjectPath();
    this.buildDirectory = builder.getBuildDirectory();
    this.project = builder.getProject();
    this.logger = project.getLogger();
    this.openAiChatModelBuilder = builder.openAiChatModelBuilder();
    this.task = builder.task();
  }

  public void init() {
    initDirectories();
    initModelPrompter();
    detectFramework();
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

  private void initModelPrompter() {
    ChatLanguageModel chatModel =
        openAiChatModelBuilder.apiKey(this.apiKey).modelName(GPT_4_O).maxTokens(MAX_TOKENS).build();
    PromptParser parser = new PromptParserImpl(logger);
    this.modelPrompter = new ModelPrompterImpl(logger, chatModel, parser);
  }

  public String getBuildDirectory() {
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
    return buildDir.getAbsolutePath();
  }

  public void invoke() {
    logger.lifecycle("Start of Invoking");


    logger.lifecycle("End of Invoking");
  }

  private void configureJacocoTestReport() {
    TaskContainer tasks = project.getTasks();
    tasks.named("jacocoTestReport", JacocoReport.class, jacocoReport -> {
      jacocoReport.setDescription("Generates JaCoCo code coverage report.");
      jacocoReport.setGroup("Verification");
      jacocoReport.getReports().getCsv().getRequired().set(true);

      // Add execution hooks for better logging
      jacocoReport.doFirst(taskOne -> {
        logger.lifecycle("Starting JaCoCo report generation " + taskOne);
      });

      jacocoReport.doLast(taskOne -> {
        logger.lifecycle("Completed JaCoCo report generation " + taskOne);
      });

      // Force the report to always run
      jacocoReport.getOutputs().upToDateWhen(taskOne -> false);
      jacocoReport.getOutputs().setPreviousOutputFiles(null);
    });
  }

  private File getCoverageReportFile() {
    TaskContainer tasks = project.getTasks();
    AtomicReference<File> file = new AtomicReference<>(new File("build/coverageReport.csv"));
    tasks.matching(taskOne -> taskOne.getName().equals("jacocoTestReport")).forEach(taskTwo -> {
      JacocoReport jacocoReport = (JacocoReport) taskTwo;
      file.set(jacocoReport.getReports().getCsv().getOutputLocation().get().getAsFile());
    });
    return file.get();
  }


  private List<File> getJavaTestSourceFiles() {
    List<File> source = new ArrayList<>();
    JavaCompile javaTestCompileTask = project.getTasks().withType(JavaCompile.class).findByName("compileTestJava");
    if (javaTestCompileTask != null) {
      FileTree sourceFiles = javaTestCompileTask.getSource();
      Set<File> sourceDirs = sourceFiles.getFiles();
      source.addAll(sourceDirs);
    }
    return source;
  }

  private List<CoverageReport> increaseTests(int iterations, File pickedTestFile, File coverageReportFile) {
    List<CoverageReport> reports = new ArrayList<>();
    for (int i = 0; i < iterations; i++) {
      Optional<CoverageReport> optionalReport = reportCoverage(pickedTestFile, coverageReportFile);
      logger.lifecycle("Coverage report {}", optionalReport);
      if (optionalReport.isPresent()) {
        CoverageReport report = optionalReport.get();
        reports.add(report);
        if (report.metPercentage()) {
          break;
        }
      }
    }
    return reports;
  }


  private Optional<CoverageReport> reportCoverage(File pickedTestFile, File coverageReportFile) {
    try {
      TestInfoResponse testInfoResponse = modelPrompter.chatter(getJavaSourceFiles(), pickedTestFile);
      String sourceFile = testInfoResponse.filepath();
      String testFile = pickedTestFile.getAbsolutePath();
      String sourceFileName = fileName(sourceFile);
      String testFileSource = loadFile(new File(testFile));
      String coverReportContent = loadFile(coverageReportFile);
      return checkReportScores(coverReportContent, testFileSource, sourceFileName);

    } catch (CoverError e) {
      logger.error("Failed to get test info for file: {}", pickedTestFile.getAbsolutePath(), e);
    }
    return Optional.empty();
  }

  private List<File> getJavaSourceFiles() {
    List<File> paths = new ArrayList<>();
    JavaCompile javaCompileTask = project.getTasks().withType(JavaCompile.class).findByName("compileJava");
    if (javaCompileTask != null) {
      FileTree sourceFiles = javaCompileTask.getSource();
      if (sourceFiles != null) {
        Set<File> sourceDirs = sourceFiles.getFiles();
        for (File srcDir : sourceDirs) {
          paths.add(srcDir);
        }
      }
    }
    return paths;
  }

  private String loadFile(File file) throws CoverError {
    StringWriter writer = new StringWriter();
    try (InputStream inputStream = Files.newInputStream(file.toPath())) {
      int data;
      while ((data = inputStream.read()) != -1) {
        writer.write(data);
      }
    } catch (Exception e) {
      logger.error("Failed to read file  " + file, e);
      throw new CoverError("Failed to read the file ", e);
    }
    return writer.toString();
  }

  private String fileName(String filePath) {
    Path path = Paths.get(filePath);
    return path.getFileName().toString();
  }

  private Optional<CoverageReport> checkReportScores(String coverageReportContent, String testSource,
                                                     String classToReportOn) {
    Optional<CoverageReport> report = Optional.empty();
    String[] lines = coverageReportContent.split("\n");
    String[] header = lines[0].split(",");

    for (int i = 1; i < lines.length; i++) {
      String[] values = lines[i].split(",");
      Map<String, String> data = new HashMap<>();
      for (int j = 0; j < header.length; j++) {
        data.put(header[j], values[j]);
      }
      String className = data.get("CLASS");
      if (classToReportOn.equals(className)) {
        int instructionMissed = Integer.parseInt(data.get("INSTRUCTION_MISSED"));
        int instructionCovered = Integer.parseInt(data.get("INSTRUCTION_COVERED"));
        int methodMissed = Integer.parseInt(data.get("METHOD_MISSED"));
        int methodCovered = Integer.parseInt(data.get("METHOD_COVERED"));
        int branchMissed = Integer.parseInt(data.get("BRANCH_MISSED"));
        int branchCovered = Integer.parseInt(data.get("BRANCH_COVERED"));

        int totalInstructions = instructionMissed + instructionCovered;
        int totalMethods = methodMissed + methodCovered;
        int totalBranches = branchMissed + branchCovered;

        double instructionMissedPercentage =
            totalInstructions > 0 ? (double) instructionMissed / totalInstructions * PERCENTAGE_CONVERT : 0;
        double methodMissedPercentage =
            totalMethods > 0 ? (double) methodMissed / totalMethods * PERCENTAGE_CONVERT : 0;
        double branchMissedPercentage =
            totalBranches > 0 ? (double) branchMissed / totalBranches * PERCENTAGE_CONVERT : 0;
        boolean metPercentage = instructionMissedPercentage > coverage && methodMissedPercentage > coverage
            && branchMissedPercentage > coverage;
        report = Optional.of(new CoverageReport(coverageReportContent, metPercentage,
            new TestClassReport(className, instructionMissedPercentage, methodMissedPercentage, branchMissedPercentage),
            testSource));
      }
    }
    return report;
  }

  private void initDirectories() {
    Directory projectDirectory = project.getLayout().getProjectDirectory();
    projectPath = projectDirectory.getAsFile().getAbsolutePath();
    logger.lifecycle("Root Project path {}", projectPath);
    File buildDir = project.getLayout().getBuildDirectory().getAsFile().get();
    if (!buildDir.exists()) {
      //logger.error("Build directory is not present, running fullJacocoBuildTestRun");
      getBuildDirectory();
    }
  }

  private void markTasksNotUpToDate(String... taskNames) {
    for (String taskName : taskNames) {
      Task taskOne = project.getTasks().findByName(taskName);
      if (taskOne != null) {
        taskOne.setDidWork(false);
        taskOne.getOutputs().upToDateWhen(t -> false);
        if (taskOne instanceof TaskInternal) {
          ((TaskInternal) taskOne).getOutputs().setPreviousOutputFiles(null);
        }
      }
    }
  }


  public List<TestingFramework> getTestingFrameworks() {
    return testingFrameworks;
  }

}
