package ai.qodo.cover.plugin.toml;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FileNumberer implements LineNumberer {

  @Override
  public String addLineNumbers(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }

    String[] lines = input.split("\\r?\\n|\\r");

    return IntStream.range(0, lines.length).mapToObj(i -> (i + 1) + " " + lines[i]).collect(Collectors.joining("\n"));

  }

}
