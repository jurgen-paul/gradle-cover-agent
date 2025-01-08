package ai.qodo.cover.plugin;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.gradle.api.logging.Logger;

public class Model {
  private final Logger logger;
  private final ChatLanguageModel model;
  private final ModelUtility utility;
  private final ModelType modelType;
  private final String apiKey;
  private final String baseUrl;

  private Model(ModelBuilder builder) {
    this.logger = builder.logger;
    this.model = builder.model;
    this.utility = builder.utility;
    this.modelType = builder.modelType;
    this.baseUrl = builder.baseUrl;
    this.apiKey = builder.apiKey;
  }

  public static ModelBuilder builder() {
    return new ModelBuilder();
  }

  @Override
  public String toString() {
    return "Model{" + "model=" + model + ", utility=" + utility + ", modelType=" + modelType + ", baseUrl='" + baseUrl
        + '\'' + ", apiKey='" + (apiKey != null ? "length=" + apiKey.length() : "null") + '\'' + '}';
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public Logger getLogger() {
    return logger;
  }

  public ChatLanguageModel getModel() {
    return model;
  }

  public ModelUtility getUtility() {
    return utility;
  }

  public ModelType getModelType() {
    return modelType;
  }

  public static class ModelBuilder {
    private Logger logger;
    private ChatLanguageModel model;
    private ModelUtility utility;
    private ModelType modelType = ModelType.GPT_4;
    private String baseUrl;
    private String apiKey;


    public ModelBuilder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    public ModelBuilder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public ModelBuilder logger(Logger logger) {
      this.logger = logger;
      return this;
    }

    public ModelBuilder model(ChatLanguageModel model) {
      this.model = model;
      return this;
    }

    public ModelBuilder utility(ModelUtility utility) {
      this.utility = utility;
      return this;
    }

    public ModelBuilder modelType(ModelType modelType) {
      this.modelType = modelType;
      return this;
    }

    public Model build() {
      return new Model(this);
    }
  }
}