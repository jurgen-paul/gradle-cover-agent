package ai.qodo.cover.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.gradle.api.logging.Logger;

public class ModelUtility {
  private Logger logger;

  public ModelUtility(Logger logger) {
    this.logger = logger;
  }

  public String extractJson(String text) {
    if (text != null) {
      return text.substring(text.indexOf('{'), text.lastIndexOf('}') + 1);
    } else {
      return "{}";
    }
  }

  public String readFile(File file) {
    String contents = "";
    try {
      contents = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
    } catch (IOException e) {
      logger.error("Error with file {} check your project you will not have accurate outcomes", file, e);
    }
    return contents;
  }


  public File createTestFile(TestFileResponse response) throws CoverError {
    try {
      String path = response.path();
      String fileName = response.fileName();

      boolean hasFileExtension = path.lastIndexOf('.') > path.lastIndexOf(File.separator);

      File testFile;
      if (hasFileExtension) {
        // If path contains file extension, get the parent directory
        File fullPath = new File(path);
        testFile = new File(fullPath.getParent(), fileName);
      } else {
        testFile = new File(path, fileName);
      }

      File parentDir = testFile.getParentFile();
      if (!parentDir.exists() && !parentDir.mkdirs()) {
        throw new CoverError("Failed to create directory structure for test file: " + testFile);
      }

      Files.writeString(testFile.toPath(), response.contents());
      return testFile;
    } catch (IOException e) {
      throw new CoverError("Failed to create test file: " + response.fileName(), e);
    }
  }

}
