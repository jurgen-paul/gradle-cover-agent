package ai.qodo.cover.plugin;

import dev.langchain4j.model.output.FinishReason;

public enum State {
  STOP(FinishReason.STOP), LENGTH(FinishReason.LENGTH), TOOL_EXECUTION(FinishReason.TOOL_EXECUTION),
  CONTENT_FILTER(FinishReason.CONTENT_FILTER), OTHER(FinishReason.OTHER), TIMEOUT, // New value
  ERROR;

  private final FinishReason finishReason;

  // Constructor to map existing enum values
  State(FinishReason finishReason) {
    this.finishReason = finishReason;
  }

  // Constructor for new values
  State() {
    this.finishReason = null;
  }

  // Static method to map FinishReason to State
  public static State fromFinishReason(FinishReason finishReason) {
    for (State state : State.values()) {
      if (state.finishReason == finishReason) {
        return state;
      }
    }
    throw new IllegalArgumentException("No matching State for FinishReason: " + finishReason);
  }

  public FinishReason getFinishReason() {
    return finishReason;
  }

}
