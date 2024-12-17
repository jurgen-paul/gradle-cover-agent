package ai.qodo.cover.plugin;

import dev.langchain4j.model.output.TokenUsage;

public record AITokenUsage(Integer inputTokenCount, Integer outputTokenCount, Integer totalTokenCount) {

    public AITokenUsage(TokenUsage tokenUsage) {
        this(tokenUsage.inputTokenCount(), tokenUsage.outputTokenCount(), tokenUsage.totalTokenCount());
    }
}
