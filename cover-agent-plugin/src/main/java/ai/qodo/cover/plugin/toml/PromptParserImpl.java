package ai.qodo.cover.plugin.toml;

import ai.qodo.cover.plugin.CoverError;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.gradle.api.logging.Logger;

public class PromptParserImpl implements PromptParser {

  private static final Map<String, String> TEMPLATES = new HashMap<>();

  static {
    try {
      TEMPLATES.put("test_generation_prompt", readTemplate("test_generation_prompt"));
      TEMPLATES.put("test_system_generation_prompt", readTemplate("test_system_generation_prompt"));
    } catch (CoverError e) {
      throw new RuntimeException(e);
    }
  }

  private final Logger logger;
  private final VelocityEngine engine;

  public PromptParserImpl(Logger logger, VelocityEngine engine) {
    this.engine = engine;
    this.logger = logger;
  }

  public PromptParserImpl(Logger logger) {
    this.logger = logger;
    Properties props = new Properties();
    engine = new VelocityEngine();
    engine.init(props);
  }

  private static String readTemplate(String templateName) throws CoverError {
    String resourcePath = "/templates/" + templateName + ".vm";
    try (InputStream inputStream = PromptParserImpl.class.getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        throw new CoverError("Template not found: " + templateName);
      }
      StringWriter writer = new StringWriter();
      int data;
      while ((data = inputStream.read()) != -1) {
        writer.write(data);
      }
      return writer.toString();
    } catch (IOException e) {
      throw new CoverError("Error loading template: " + templateName, e);
    }

  }

  public String prompt(String template, Context context) {
    StringWriter writer = new StringWriter();
    engine.evaluate(context, writer, template, TEMPLATES.get(template));
    return writer.toString();
  }

}
