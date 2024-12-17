package ai.qodo.cover.plugin;

import java.io.File;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.junit.JUnitOptions;
import org.gradle.api.tasks.testing.junitplatform.JUnitPlatformOptions;
import org.gradle.api.tasks.testing.logging.TestLoggingContainer;
import org.gradle.api.tasks.testing.testng.TestNGOptions;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension;
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension;
import org.gradle.testing.jacoco.tasks.JacocoReport;

public class CoverAgentPlugin implements Plugin<Project> {
  static final Integer DEFAULT_ITERATIONS = 1;
  static final Integer DEFAULT_PERCENTAGE = 75;

  @Override
  public void apply(Project project) {
    Logger log = project.getLogger();
    log.info("Running plugin version {}", "0.0.1");

    // Create the extension to hold configuration properties
    CoverAgentExtension extension = project.getExtensions().create("coverAgent", CoverAgentExtension.class, project);

    // Register the coverAgentTask
    project.getTasks().register("coverAgentTask", CoverAgentTask.class, task -> {
      task.setGroup("verification");
      task.setDescription("Runs the cover agent task attempting to increase code coverage");

      // Configure task properties from the extension
      if (extension != null) {
        task.getApiKey().set(extension.getApiKey());
        task.getModel().set(extension.getModel());
        task.getCoverAgentBinaryPath().set(extension.getCoverAgentBinaryPath());
        task.getIterations().set(extension.getIterations().getOrElse(DEFAULT_ITERATIONS));
        task.getCoverage().set(extension.getCoverage().getOrElse(DEFAULT_PERCENTAGE));
      }
      task.getOutputs().upToDateWhen(t -> false);
    });

    // After the project is evaluated, configure repeated tasks
    project.afterEvaluate(proj -> {
      Integer iterations = extension.getIterations().getOrElse(DEFAULT_ITERATIONS);
      configureRepeats(project, iterations);
    });
  }

  // Method to configure repeated tasks based on iterations
  private void configureRepeats(Project project, int iterations) {
    Logger log = project.getLogger();
    TaskContainer tasks = project.getTasks();

    // Get references to the existing tasks
    if (!project.getPlugins().hasPlugin(JacocoPlugin.class)) {
      project.getPlugins().apply(JacocoPlugin.class);
    }
    final JavaCompile originalJavaCompileTask = (JavaCompile) tasks.findByName("compileJava");
    final JavaCompile originalTestCompileTask = (JavaCompile) tasks.findByName("compileTestJava");
    final Test originalTestTask = (Test) tasks.findByName("test");
    final JacocoReport originalJacocoReportTask = (JacocoReport) tasks.findByName("jacocoTestReport");
    final JavaExec javaRuntimeTask = (JavaExec) tasks.findByName("run");
    if (javaRuntimeTask != null) {
      log.lifecycle("Java Runtime JVM ARGS: " + javaRuntimeTask.getJvmArgs());
    } else {
      log.warn("Java runtime task 'run' not found");
    }
    JacocoPluginExtension jacocoExt = project.getExtensions().findByType(JacocoPluginExtension.class);
    if (jacocoExt == null) {
      jacocoExt = project.getExtensions().create("jacoco", JacocoPluginExtension.class);
      log.lifecycle("JacocoPluginExtension created agent path");
    } else {
      log.lifecycle("JacocoPluginExtension already exists");
    }

    log.lifecycle("TEST JVM ARGS" + originalTestTask.getJvmArgs());
    log.lifecycle("Java Execute path " + originalJavaCompileTask.getJavaCompiler().get().getExecutablePath());

    JacocoPluginExtension jacocoExtension = project.getExtensions().getByType(JacocoPluginExtension.class);

    // Configure all Test tasks
    project.getTasks().withType(Test.class).configureEach(testTask -> {
      log.lifecycle("Configuring testTask " + testTask);
      // Get or create the JacocoTaskExtension for the test task
      JacocoTaskExtension jacocoTaskExtensionRef = testTask.getExtensions().findByType(JacocoTaskExtension.class);
      if (jacocoTaskExtensionRef == null) {
        jacocoTaskExtensionRef = testTask.getExtensions().create("jacoco", JacocoTaskExtension.class, testTask);
      }
      final JacocoTaskExtension jacocoTaskExtension = jacocoTaskExtensionRef;

      // Before the test task executes, enhance it
      testTask.doFirst(task -> {
        // Resolve the JaCoCo agent configuration to get the agent JAR file
        Configuration jacocoAgentConf = project.getConfigurations().getByName("jacocoAgent");
        FileCollection jacocoAgentFiles = jacocoAgentConf.getAsFileTree();

        // Ensure we have the JaCoCo agent JAR
        File jacocoAgentFile = jacocoAgentFiles.getSingleFile();

        // Destination file for the coverage data
        File destFile = jacocoTaskExtension.getDestinationFile();

        // Construct the JVM argument to include the JaCoCo agent
        String jacocoAgentArgument =
            "-javaagent:" + jacocoAgentFile.getAbsolutePath() + "=destfile=" + destFile.getAbsolutePath();

        // Add the JaCoCo agent to the JVM arguments of the test task
        testTask.jvmArgs(jacocoAgentArgument);

        log.lifecycle("jacocAgent is set " + jacocoAgentArgument);
      });
    });
  }

  // Helper method to copy compile options
  private void copyCompileOptions(CompileOptions targetOptions, CompileOptions sourceOptions) {
    targetOptions.setEncoding(sourceOptions.getEncoding());
    targetOptions.setCompilerArgs(sourceOptions.getCompilerArgs());
    targetOptions.setDebug(sourceOptions.isDebug());
    targetOptions.setDebugOptions(sourceOptions.getDebugOptions());
    targetOptions.setFork(sourceOptions.isFork());
    targetOptions.setForkOptions(sourceOptions.getForkOptions());
    targetOptions.setIncremental(sourceOptions.isIncremental());
    targetOptions.setAnnotationProcessorPath(sourceOptions.getAnnotationProcessorPath());
    // Copy other relevant options as needed
  }

  // Helper method to copy test logging options
  private void copyTestLogging(TestLoggingContainer targetLogging, TestLoggingContainer sourceLogging) {
    targetLogging.setEvents(sourceLogging.getEvents());
    targetLogging.setExceptionFormat(sourceLogging.getExceptionFormat());
    targetLogging.setShowExceptions(sourceLogging.getShowExceptions());
    targetLogging.setShowCauses(sourceLogging.getShowCauses());
    targetLogging.setShowStackTraces(sourceLogging.getShowStackTraces());
    targetLogging.setShowStandardStreams(sourceLogging.getShowStandardStreams());
    targetLogging.setMinGranularity(sourceLogging.getMinGranularity());
    targetLogging.setMaxGranularity(sourceLogging.getMaxGranularity());
    // Copy other relevant logging options as needed
  }

  // Helper method to configure the test framework
  private void configureTestFramework(Test targetTask, Test sourceTask) {
    // Check which test framework is used in the original task
    if (sourceTask.getOptions() instanceof JUnitOptions) {
      // Using JUnit
      targetTask.useJUnit(jUnitOptions -> {
        JUnitOptions sourceOptions = (JUnitOptions) sourceTask.getOptions();
        jUnitOptions.setIncludeCategories(sourceOptions.getIncludeCategories());
        jUnitOptions.setExcludeCategories(sourceOptions.getExcludeCategories());
        jUnitOptions.copyFrom(sourceOptions);
      });
    } else if (sourceTask.getOptions() instanceof JUnitPlatformOptions) {
      // Using JUnit Platform
      targetTask.useJUnitPlatform(jUnitPlatformOptions -> {
        JUnitPlatformOptions sourceOptions = (JUnitPlatformOptions) sourceTask.getOptions();
        jUnitPlatformOptions.setIncludeTags(sourceOptions.getIncludeTags());
        jUnitPlatformOptions.setExcludeTags(sourceOptions.getExcludeTags());
        jUnitPlatformOptions.setIncludeEngines(sourceOptions.getIncludeEngines());
        jUnitPlatformOptions.setExcludeEngines(sourceOptions.getExcludeEngines());
        jUnitPlatformOptions.copyFrom(sourceOptions);
      });
    } else if (sourceTask.getOptions() instanceof TestNGOptions) {
      // Using TestNG
      targetTask.useTestNG(testNGOptions -> {
        TestNGOptions sourceOptions = (TestNGOptions) sourceTask.getOptions();
        testNGOptions.setSuiteXmlFiles(sourceOptions.getSuiteXmlFiles());
        testNGOptions.copyFrom(sourceOptions);
      });
    } else {
      // Default to JUnit if no specific framework is detected
      targetTask.useJUnit();
    }
  }
}
