package ai.qodo.cover.plugin

import ai.qodo.cover.plugin.toml.FileNumberer
import ai.qodo.cover.plugin.toml.PromptParserImpl
import com.google.gson.Gson
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.openai.OpenAiChatModel
import org.apache.velocity.VelocityContext
import org.gradle.api.logging.Logger
import spock.lang.Specification

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O

class AIPromptAnalysisSpec extends Specification {

    def "test reading file works "() {
        given:
        Logger logger = Mock(Logger)
        String apiKey = System.getenv("OPENAI_API_KEY")

        ChatLanguageModel model = OpenAiChatModel.builder().apiKey(apiKey).modelName(GPT_4_O).maxTokens(5000).build()

        PromptParserImpl promptParser = new PromptParserImpl(logger)
        File testJavaResource = new File('src/test/resources/CalcSpec.groovy')
        File srcJavaResource = new File('src/test/resources/Calc.java')
        File jsonSchema = new File('src/main/resources/templates/test_response.json')

        Map<String, String> keys = ["language"              : "java"
                                    , "language_test"       : "groovy"
                                    , "source_file_name"    : "Calc.java"
                                    , "source_file_numbered": new FileNumberer().addLineNumbers(srcJavaResource.text).trim()
                                    , "testing_framework"   : "spock"
                                    , "test_file_name"      : "CalcSpec.groovy"
                                    , "test_file"           : testJavaResource.text.trim()
                                    , "code_coverage_report": "GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED\n" + "JaCoCo Coverage Report,com.davidparry.cover.test,ExampleCode,4,19,0,0,1,5,1,5,1,5\n" + "JaCoCo Coverage Report,com.davidparry.cover.test,Fibonacci,0,41,0,8,0,13,0,6,0,2"
                                    , "jsonSchema"          : jsonSchema.text.trim()
                                    , "percentage"          : "90%"]

        VelocityContext context = new VelocityContext(keys)

        ModelPrompter modelPrompter = new ModelPrompterImpl(logger, model, promptParser)

        when:
        ModelAskResponse response = modelPrompter.testChatter(context)

        then:
        response != null
        println(response)
    }

}
