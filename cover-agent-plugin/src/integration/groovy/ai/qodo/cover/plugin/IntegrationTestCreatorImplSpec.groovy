package ai.qodo.cover.plugin

import ai.qodo.cover.plugin.toml.PromptParser
import ai.qodo.cover.plugin.toml.PromptParserImpl
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.openai.OpenAiChatModel
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.process.ExecResult
import spock.lang.Specification

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O

class IntegrationTestCreatorImplSpec extends Specification {
    // Initialize TestCreatorImpl with valid parameters and verify object creation
    def "should initialize TestCreatorImpl with valid parameters"() {
        given:
        Logger logger = Mock(Logger)
        ChatLanguageModel model = OpenAiChatModel.builder().apiKey(System.getenv("OPENAI_API_KEY")).modelName(GPT_4_O).maxTokens(5000).build()

        def sourceFilePath = "src/test/resources/Calc.java"
        def testFilePath = "src/test/resources/CalcSpec.groovy"
        Project project = Mock(Project)
        def jacocoCoverageCommand = "jacocoTestReport"
        def projectPath = "build"
        PromptParser promptParser = new PromptParserImpl(logger)
        ModelPrompter prompter = new ModelPrompterImpl(logger, model, promptParser)
        def coveragePercentage = 80
        def iterations = 5
        File coverageReportFile = new File("src/integration/resources/jacocoTestReport.csv")
        ExecResult prjExecResult = Mock(ExecResult)


        when:
        TestCreator testCreator = new TestCreatorImpl(sourceFilePath, testFilePath, project, jacocoCoverageCommand
                , projectPath, prompter, coveragePercentage, iterations, logger, coverageReportFile)
        boolean res = testCreator.execute()



        then:
        _ * project.exec(_) >> prjExecResult

        testCreator != null
    }
}
