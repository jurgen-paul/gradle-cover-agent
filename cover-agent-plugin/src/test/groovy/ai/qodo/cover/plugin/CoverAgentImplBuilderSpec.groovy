package ai.qodo.cover.plugin

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import spock.lang.Specification

class CoverAgentImplBuilderSpec extends Specification {
    void setup() {
    }
    // Builder correctly initializes all fields
    def "should initialize all fields correctly when built"() {
        given:
        Project project = Mock(Project)
        Logger logger = Mock(Logger)
        def builder = new CoverAgentBuilder()
                .apiKey("testApiKey")
                .model("testWanDBApiKey")
                .iterations(5)
                .coverage(80)
                .coverAgentBinaryPath("/path/to/binary")
                .modelPrompter(Mock(ModelPrompterImpl))
                .javaClassPath(Optional.of("/path/to/class"))
                .javaTestClassPath(Optional.of("/path/to/test/class"))
                .projectPath("/path/to/project")
                .javaClassDir(Optional.of("/path/to/class/dir"))
                .buildDirectory("/path/to/build")
                .coverAgentExecutor(Mock(CoverAgentExecutor))
                .project(project)

        when:
        CoverAgentImpl coverAgentImpl = builder.build()

        then:
        1 * project.getLogger() >> logger
        coverAgentImpl.apiKey == "testApiKey"
        coverAgentImpl.wanDBApiKey == "testWanDBApiKey"
        coverAgentImpl.iterations == 5
        coverAgentImpl.coverage == 80
        coverAgentImpl.coverAgentBinaryPath == "/path/to/binary"
        coverAgentImpl.modelPrompter != null
        coverAgentImpl.javaClassPath.get() == "/path/to/class"
        coverAgentImpl.javaTestClassPath.get() == "/path/to/test/class"
        coverAgentImpl.projectPath == "/path/to/project"
        coverAgentImpl.javaClassDir.get() == "/path/to/class/dir"
        coverAgentImpl.buildDirectory == "/path/to/build"
        coverAgentImpl.coverAgentExecutor != null
        coverAgentImpl.project != null
    }

}
