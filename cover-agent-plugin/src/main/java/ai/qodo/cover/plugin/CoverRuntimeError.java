package ai.qodo.cover.plugin;

public class CoverRuntimeError extends RuntimeException {
  public CoverRuntimeError(String message, Throwable cause) {
    super(message, cause);
  }

  public CoverRuntimeError(String message) {
    super(message);
  }
}
