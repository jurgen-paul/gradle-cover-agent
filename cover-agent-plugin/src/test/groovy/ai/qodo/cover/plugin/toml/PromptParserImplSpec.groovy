package ai.qodo.cover.plugin.toml

import org.apache.velocity.VelocityContext
import spock.lang.Specification

class PromptParserImplSpec extends Specification {

    def "test reading file works "() {
        given:
        PromptParserImpl promptParser = new PromptParserImpl(null)
        Map<String, String> keys = ["language"                      : "java"
                                    , "language_test"               : "java"
                                    , "source_file_name"            : "Calc.java"
                                    , "source_file_numbered"        : "sourcefile numbered content"
                                    , "testing_framework"           : "spock"
                                    , "test_file_name"              : "CalcTest.java"
                                    , "test_file"                   : "test file contents"
                                    , "additional_includes_section" : "additional includes for the test "
                                    , "failed_tests_section"        : "failed tests would go here"
                                    , "additional_instructions_text": "Additional instructions would go here"
                                    , "code_coverage_report"        : "coverage report of prior run will go here should always have one"
                                    , "jsonSchema"                  : "valid json schema definition"]

        VelocityContext context = new VelocityContext(keys)

        when:
        String p = promptParser.prompt("test_generation_prompt", context)

        then:
        p.length() > 0
        for (String key : keys.keySet()) {
            if (!p.contains(keys.get(key))) {
                throw new RuntimeException("Failed on Key " + key + " missing value " + keys.get(key))
            }
        }
        println(p)


    }
}
