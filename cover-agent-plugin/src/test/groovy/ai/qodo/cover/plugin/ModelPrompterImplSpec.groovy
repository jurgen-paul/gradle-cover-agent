package ai.qodo.cover.plugin

import ai.qodo.cover.plugin.toml.PromptParser
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.output.Response
import org.gradle.api.logging.Logger
import spock.lang.Specification

class ModelPrompterImplSpec extends Specification {
    private String validJson = """
{
  "testingFramework": "spock",
  "newTests": [
    {
      "testBehavior": "Test subtraction method with positive integers",
      "linesCovered": [9, 10, 11, 13],
      "methodSignatureFromSource": "public static int sub(int num1, int num2)",
      "lineNumbersOfSource": [9, 11],
      "newImportsForTestClass": [],
      "newTestMethod": "def \\"test subtraction with positive integers\\"() {\\n    expect:\\n    Calc.sub(5, 3) == 2\\n}",
      "inDepthDescription": "This test verifies the correctness of the subtraction method when both input values are positive integers. The test ensures that the method correctly subtracts the second integer from the first and returns the expected result."
    },
    {
      "testBehavior": "Test subtraction method with negative integers",
      "linesCovered": [9, 10, 11, 13],
      "methodSignatureFromSource": "public static int sub(int num1, int num2)",
      "lineNumbersOfSource": [9, 11],
      "newImportsForTestClass": [],
      "newTestMethod": "def \\"test subtraction with negative integers\\"() {\\n    expect:\\n    Calc.sub(-5, -3) == -2\\n}",
      "inDepthDescription": "This test evaluates the subtraction method when both input values are negative integers. It checks that the subtraction operation is correctly performed, yielding the expected result of subtracting the second negative integer from the first."
    },
    {
      "testBehavior": "Test subtraction method with mixed sign integers",
      "linesCovered": [9, 10, 11, 13],
      "methodSignatureFromSource": "public static int sub(int num1, int num2)",
      "lineNumbersOfSource": [9, 11],
      "newImportsForTestClass": [],
      "newTestMethod": "def \\"test subtraction with mixed sign integers\\"() {\\n    expect:\\n    Calc.sub(5, -3) == 8\\n}",
      "inDepthDescription": "This test checks the behavior of the subtraction method when one input value is positive and the other is negative. It confirms that the method handles mixed signs correctly and returns the sum of the positive integer and the absolute value of the negative integer."
    },
    {
      "testBehavior": "Test subtraction method with zero",
      "linesCovered": [9, 10, 11, 13],
      "methodSignatureFromSource": "public static int sub(int num1, int num2)",
      "lineNumbersOfSource": [9, 11],
      "newImportsForTestClass": [],
      "newTestMethod": "def \\"test subtraction with zero\\"() {\\n    expect:\\n    Calc.sub(5, 0) == 5\\n    Calc.sub(0, 5) == -5\\n}",
      "inDepthDescription": "This test checks the behavior of the subtraction method when zero is one of the operands. It ensures that subtracting zero from a positive number returns the positive number, and subtracting a positive number from zero returns the negative of that number, demonstrating correct handling of zero as an operand."
    }
  ]
}
"""

    def "should log user prompt to model"() {
        given:
        Logger logger = Mock(Logger)
        ChatLanguageModel model = Mock(ChatLanguageModel)
        PromptParser promptParser = Mock(PromptParser)
        ModelPrompterImpl prompter = new ModelPrompterImpl(logger, model, promptParser)
        List<File> sourceFiles = [new File("src/test/java/TestFile1.java")]
        File testFile = new File('src/test/resources/Calc.java')
        Response<AiMessage> rsp = Mock(Response<AiMessage>)
        AiMessage message = Mock(AiMessage)
        when:
        TestInfoResponse response = prompter.chatter(sourceFiles, testFile)

        then:
        1 * model.generate(_, _) >> rsp
        _ * rsp.content() >> message
        _ * message.text() >> 'json     {\"filepath\": \"path_here\"} '
        response.filepath() == "path_here"
    }

    def "failure can not contact or call model error"() {
        given:
        Logger logger = Mock(Logger)
        ChatLanguageModel model = Mock(ChatLanguageModel)
        PromptParser promptParser = Mock(PromptParser)
        ModelPrompterImpl prompter = new ModelPrompterImpl(logger, model, promptParser)
        List<File> sourceFiles = [new File("src/test/java/TestFile1.java")]
        File testFile = new File("src/test/java/TestFile.java")

        when:
        prompter.chatter(sourceFiles, testFile)

        then:
        thrown(CoverError)
    }

    def "extract empty no value from model empty object"() {
        given:
        Logger logger = Mock(Logger)
        ChatLanguageModel model = Mock(ChatLanguageModel)
        ModelPrompterImpl prompter = new ModelPrompterImpl(logger, model, null)

        expect:
        prompter.extractJson("```json" + validJson + "```") != validJson
    }

    def "extract empty no value from model empty object"() {
        given:
        Logger logger = Mock(Logger)
        ChatLanguageModel model = Mock(ChatLanguageModel)
        ModelPrompterImpl prompter = new ModelPrompterImpl(logger, model, null)

        expect:
        prompter.extractJson(null) == "{}"
    }

}
