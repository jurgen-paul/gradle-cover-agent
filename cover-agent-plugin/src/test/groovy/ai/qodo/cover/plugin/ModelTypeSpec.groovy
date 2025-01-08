package ai.qodo.cover.plugin

import dev.langchain4j.model.openai.OpenAiChatModelName
import spock.lang.Specification

class ModelTypeSpec extends Specification {

    // Enum ModelType should contain GPT_4 and LLAMA2 values
    def "should contain expected enum values"() {
        expect:
        ModelType.values().length == 2
        ModelType.values().contains(ModelType.GPT_4)
        ModelType.values().contains(ModelType.LLAMA2)
    }

    // Constructor with String parameter should set modelName correctly
    def "should set correct model name when using String constructor"() {
        when:
        def model = ModelType.GPT_4

        then:
        model.getModelName() == "openai/gpt-4o"
    }

    // Passing empty string to String constructor should be handled
    def "should accept empty string as model name"() {
        given:
        def emptyModelName = ""

        when:
        def model = ModelType.valueOf(emptyModelName)

        then:
        thrown(IllegalArgumentException)
    }

    // a valid enum when using valueOf for the string
    def "should return correct enum when using valueOf with valid string"() {
        expect:
        ModelType.valueOf("GPT_4") == ModelType.GPT_4
        ModelType.valueOf("LLAMA2") == ModelType.LLAMA2
    }
    // Returns GPT_4 enum when input is 'gpt-4o'
    def "should return GPT_4 enum when input is gpt-4o"() {
        when:
        def result = ModelType.fromModelName("openai/gpt-4o")

        then:
        result == ModelType.GPT_4
    }
    // Returns LLAMA2 enum when input is 'llama2'
    def "should return LLAMA2 enum when input is llama2"() {
        when:
        def result = ModelType.fromModelName("ollama/mistral")

        then:
        result == ModelType.LLAMA2
    }

    // Throws CoverRuntimeError for empty string input
    def "should throw CoverRuntimeError when input is empty string"() {
        when:
        ModelType.fromModelName("")

        then:
        def error = thrown(CoverRuntimeError)
        error.message == "Unsupported model name: "
    }

    // Throws CoverRuntimeError for whitespace-only input
    def "should throw CoverRuntimeError when input contains only whitespace"() {
        when:
        ModelType.fromModelName("   ")

        then:
        def error = thrown(CoverRuntimeError)
        error.message == "Unsupported model name:    "
    }
}
