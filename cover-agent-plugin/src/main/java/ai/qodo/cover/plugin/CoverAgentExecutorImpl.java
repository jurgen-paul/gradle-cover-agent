package ai.qodo.cover.plugin;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

public class CoverAgentExecutorImpl implements CoverAgentExecutor {
  public static final String OPENAI_API_KEY = "OPENAI_API_KEY";
  private final String coverAgentBinaryPath;
  private final int coverage;
  private final int iterations;
  private final Model model;

  @Override
  public String toString() {
    return "CoverAgentExecutor{" +
        "coverAgentBinaryPath='" + coverAgentBinaryPath + '\'' +
        ", coverage=" + coverage +
        ", iterations=" + iterations +
        ", model=" + model +
        '}';
  }

  private CoverAgentExecutorImpl(Builder builder) {
    this.coverAgentBinaryPath = builder.coverAgentBinaryPath;
    this.model = builder.model;
    this.coverage = builder.coverage;
    this.iterations = builder.iterations;
  }

  public static CoverAgentExecutorImpl.Builder builder() {
    return new CoverAgentExecutorImpl.Builder();
  }

  public String execute(Project project, String sourceFile, String testFile, String jacocoReportPath,
                        String commandString, String projectPath) throws CoverError {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    ExecResult result =
        project.exec(getExecSpecAction(sourceFile, testFile, jacocoReportPath, commandString, projectPath));

    if (result.getExitValue() != 0) {
      String errorOutput = errorStream.toString(StandardCharsets.UTF_8);
      throw new CoverError("An error occurred while executing coverage agent " + result + "\n" + errorOutput);
    }
    String output = "Invalid encoding";
    output = outputStream.toString(StandardCharsets.UTF_8);
    return output;
  }

  private Action<ExecSpec> getExecSpecAction(String sourceFile, String testFile, String jacocoReportPath,
                                             String commandString, String projectPath) {
    //TODO: refactor need really just the model string to pass to command and base url
    return (ExecSpec execSpec) -> {
      if (model.getApiKey() != null) {
        execSpec.environment(OPENAI_API_KEY, model.getApiKey());
      }
      execSpec.commandLine(coverAgentBinaryPath, "--source-file-path=" + sourceFile, "--test-file-path=" + testFile,
          "--code-coverage-report-path=" + jacocoReportPath, "--test-command=" + commandString,
          "--test-command-dir=" + projectPath, "--coverage-type=jacoco", "--desired-coverage=" + coverage,
          "--max-iterations=" + iterations, "--api_base=" + model.getBaseUrl(),
          "--llm_model=" + model.getModelType().getModelName());
      execSpec.setWorkingDir(projectPath);
    };
  }

  public static class Builder {
    private String coverAgentBinaryPath;
    private Model model;
    private int coverage;
    private int iterations;

    public Builder coverAgentBinaryPath(String coverAgentBinaryPath) {
      this.coverAgentBinaryPath = coverAgentBinaryPath;
      return this;
    }

    public Builder coverage(int coverage) {
      this.coverage = coverage;
      return this;
    }

    public Builder iterations(int iterations) {
      this.iterations = iterations;
      return this;
    }

    public Builder model(Model model) {
      this.model = model;
      return this;
    }

    public CoverAgentExecutorImpl build() {
      return new CoverAgentExecutorImpl(this);
    }
  }
}
