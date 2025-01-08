package ai.qodo.cover.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.gradle.api.logging.Logger;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class TestFileGeneratorImpl implements TestFileGenerator {
  private static final String TEMPLATE_PATH = "/test_source.stg";
  private final Logger logger;
  private final Map<String, File> testSourceDirectory;
  private static final String JAVA_KEY = "java";
  private static final String GROOVY_KEY = "groovy";

  public TestFileGeneratorImpl(Map<String, File> testSourceDirectory, Logger logger) {
    this.testSourceDirectory = testSourceDirectory;
    this.logger = logger;
  }

  public Optional<File> generate(File sourceFile, String framework) {
    Optional<File> response = Optional.empty();
    try {
      String className = sourceFile.getName().replace(".java", "").replace(".groovy", "");
      String packageName = extractPackageName(sourceFile);
      //String path = TestFileGeneratorImpl.class.getResource(TEMPLATE_PATH).toURI().getPath();
      //STGroup group = new STGroupFile(path, "UTF-8", '<', '>');
      STGroup group = new STGroupFile(TestFileGeneratorImpl.class.getResource(TEMPLATE_PATH), "UTF-8", '<', '>');     ST template = group.getInstanceOf(framework);
      template.add("package", packageName);
      template.add("className", className);

      String suffix = framework.equalsIgnoreCase("spockframework") ? "Spec.groovy" : "Test.java";
      String packagePath = packageName != null ? packageName.replace('.', File.separatorChar) : "";
      File packageDir = new File(testSourceDirectory.get(translate(framework)).getAbsolutePath(), packagePath);
      if (!packageDir.exists()) {
        if (!packageDir.mkdirs()) {
          logger.error("Failed to create package directories: {}", packageDir.getAbsolutePath());
          return Optional.empty();
        }
      }
      logger.debug("The root directory to write the test file to will be {}", packageDir.getAbsolutePath());

      File testFile = new File(packageDir, className + suffix);
      try (FileWriter writer = new FileWriter(testFile)) {
        writer.write(template.render());
        response = Optional.of(testFile);
      }
    } catch (Exception e) {
      logger.error("Failed to create new test file for source file {} testing framework {}", sourceFile, framework, e);
    }
    return response;
  }

  private static String translate(String framework) {
    return switch (framework.toLowerCase()) {
      case "junit4", "junit5", "junit3", "testng" -> JAVA_KEY;
      case "spockframework" -> GROOVY_KEY;
      default -> throw new IllegalArgumentException("Unknown framework: " + framework);
    };
  }

  private String extractPackageName(File sourceFile) throws CoverError {
    try {
      String content = java.nio.file.Files.readString(sourceFile.toPath());
      if (content.contains("package")) {
        return content.split("package")[1].split(";")[0].trim();
      }
    } catch (IOException e) {
      throw new CoverError("Failed to read source file: " + sourceFile.getAbsolutePath(), e);
    }
    return null;
  }
}
