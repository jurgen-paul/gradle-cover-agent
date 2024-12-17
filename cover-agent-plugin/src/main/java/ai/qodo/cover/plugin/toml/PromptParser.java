package ai.qodo.cover.plugin.toml;

import org.apache.velocity.context.Context;

public interface PromptParser {
  String prompt(String template, Context context);
}
