package ai.qodo.cover.plugin

import com.google.gson.Gson
import spock.lang.Specification

class GeneratedTestResponseSpec extends Specification {
    private static final Gson GSON = new Gson()
    def"test the response can create object"() {
        given:

        File responseFile  = new File('src/test/resources/model_response.json')

        String jsonOut = responseFile.text
        println(jsonOut)

        when:
        GeneratedTestResponse testResponse =  GSON.fromJson(jsonOut,GeneratedTestResponse.class)

        then:
        noExceptionThrown()
        testResponse != null
        testResponse.newTests().size() == 2

    }

}
