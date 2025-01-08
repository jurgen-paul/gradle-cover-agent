package ai.qodo.cover.plugin;


import dev.langchain4j.model.openai.OpenAiChatModelName;

public enum ModelType {
  GPT_4("gpt-4o"), LLAMA2("llama2");

  private final String modelName;

  ModelType(String modelName) {
    this.modelName = modelName;
  }

  ModelType(OpenAiChatModelName modelName) {
    this.modelName = modelName.toString();
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
}
