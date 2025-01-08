package ai.qodo.cover.plugin;


import dev.langchain4j.model.openai.OpenAiChatModelName;

public enum ModelType {
  GPT_4("gpt-4o", "gpt-4o"), LLAMA2("llama2", "ollama/llama2");

  private final String modelName;
  private final String fullName;

  ModelType(String modelName, String fullName) {
    this.modelName = modelName;
    this.fullName = fullName;
  }


  public static ModelType fromModelName(String modelName) {
    for (ModelType type : ModelType.values()) {
      if (type.getModelName().equalsIgnoreCase(modelName)) {
        return type;
      }
    }
    throw new CoverRuntimeError("Unsupported model name: " + modelName);
  }

  public String getModelName() {
    return modelName;
  }

  public String getFullName() {
    return fullName;
  }
}
