package ai.qodo.cover.plugin;

public record ModelAskResponse(GeneratedTestResponse answer, State state, AITokenUsage tokenUsage) {
}
