package ai.qodo.cover.plugin.toml

import spock.lang.Specification

class FileNumbererSpec extends Specification {

    // Returns input with line numbers for multi-line strings
    def "should return input with line numbers for multi-line strings"() {
        given:
        def fileNumberer = new FileNumberer()

        when:
        def output = fileNumberer.addLineNumbers(input)

        then:
        output == result

        where:
        input                          | result
        "line1\nline2\nline3"          | "1 line1\n2 line2\n3 line3"
        "line1\rline2\nline3\r\nline4" | "1 line1\n2 line2\n3 line3\n4 line4"
        "line1\rline2\nline3\rline4"   | "1 line1\n2 line2\n3 line3\n4 line4"
        null                           | null
        "single line"                  | "1 single line"
        ""                             | ""

    }

}
