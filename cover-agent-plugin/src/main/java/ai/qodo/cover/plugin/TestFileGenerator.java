package ai.qodo.cover.plugin;

import java.io.File;
import java.util.Optional;

public interface TestFileGenerator{
  Optional<File> generate(File sourceFile, String framework);
}
