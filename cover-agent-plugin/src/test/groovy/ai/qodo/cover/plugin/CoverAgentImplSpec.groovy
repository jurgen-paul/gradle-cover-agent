package ai.qodo.cover.plugin

import dev.langchain4j.model.openai.OpenAiChatModel
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.*
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskCollection
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.compile.CompileOptions
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.process.ExecResult
import spock.lang.Shared
import spock.lang.Specification
import static CoverAgentImpl.MAX_TOKENS

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O

class CoverAgentImplSpec extends Specification {

    @Shared
    CoverAgentBuilder builder = CoverAgentBuilder.builder()

    void setup() {}

    def "Not Found JavaCompileTask null "() {
        given:
        Project project = Mock(Project)
        builder.project(project)
        CoverAgentImpl coverAgent = builder.build()
        TaskContainer container = Mock(TaskContainer)
        TaskCollection collection = Mock(TaskCollection)

        when:
        coverAgent.setJavaTestClassPath()

        then:
        1 * project.getTasks() >> container
        1 * container.withType(JavaCompile.class) >> collection
        1 * collection.findByName("compileTestJava") >> null
    }

    def "init directories "() {
        given:
        Logger logger = Mock(Logger)
        Project project = Mock(Project)
        builder.project(project)
        ProjectLayout projectLayout = Mock(ProjectLayout)
        Directory projectDirectory = Mock(Directory)
        File direct = Mock(File)
        DirectoryProperty directoryProperty = Mock(DirectoryProperty)
        Provider buildDirectoryProvider = Mock(Provider)
        File realFile = new File('src/test/resources/build.gradle')

        when:
        CoverAgentImpl coverAgent = builder.build()
        coverAgent.initDirectories()

        then:
        2 * project.getLayout() >> projectLayout
        1 * projectLayout.getProjectDirectory() >> projectDirectory
        1 * projectDirectory.getAsFile() >> realFile
        1 * projectLayout.getBuildDirectory() >> directoryProperty
        1 * directoryProperty.getAsFile() >> buildDirectoryProvider
        1 * buildDirectoryProvider.get() >> direct
        1 * project.getLogger() >> logger
        1 * direct.exists() >> false
        1 * direct.mkdirs() >> outcome

        where:
        outcome << [true, false]

    }

    // need to mock out javaCompileCommand
    def "init method initializes correctly"() {
        given:
        Logger logger = Mock(Logger)
        TaskContainer container = Mock(TaskContainer)
        TaskCollection collection = Mock(TaskCollection)
        JavaCompile javaTestCompileTask = Mock(JavaCompile)
        DirectoryProperty directoryProperty = Mock(DirectoryProperty)
        Directory directory = Mock(Directory)
        File file = Mock(File)
        FileCollection fileCollection = Mock(FileCollection)
        File realFile = new File('src/test/resources/build.gradle')
        Set<File> fileSet = Set.of(realFile)
        FileTree fileTree = Mock(FileTree)

        ProjectLayout projectLayout = Mock(ProjectLayout)
        Directory projectDirectory = Mock(Directory)

        Provider buildDirectoryProvider = Mock(Provider)

        Project project = Mock(Project)
        OpenAiChatModel.OpenAiChatModelBuilder aiChatModelBuilder = Mock(OpenAiChatModel.OpenAiChatModelBuilder)
        OpenAiChatModel aiChatModel = Mock(OpenAiChatModel)
        builder.project(project).openAiChatModelBuilder(aiChatModelBuilder)

        CompileOptions javaCompileOptions = Mock(CompileOptions)

        ConfigurationContainer configurationContainer = Mock(ConfigurationContainer)

        when:
        CoverAgentImpl coverAgent = builder.build()
        coverAgent.init()

        then:
        1 * project.getConfigurations() >> configurationContainer
        1 * configurationContainer.findByName("testImplementation") >> null
        1 * configurationContainer.findByName("testCompile") >> null
        1 * logger.lifecycle("No test dependencies configuration found.")

        2 * javaTestCompileTask.getOptions() >> javaCompileOptions
        2 * javaCompileOptions.getAllCompilerArgs() >> [""]

        1 * aiChatModelBuilder.apiKey(_) >> aiChatModelBuilder
        1 * aiChatModelBuilder.modelName(GPT_4_O) >> aiChatModelBuilder
        1 * aiChatModelBuilder.maxTokens(MAX_TOKENS) >> aiChatModelBuilder
        1 * aiChatModelBuilder.build() >> aiChatModel
        1 * project.getLogger() >> logger
        1 * logger.debug("Root Project path {}", _)
        1 * logger.debug("Build directory already exists: {}", _)

        4 * project.getTasks() >> container
        4 * container.withType(JavaCompile.class) >> collection
        4 * collection.findByName(_) >> javaTestCompileTask
        5 * javaTestCompileTask.getDestinationDirectory() >> directoryProperty
        5 * directoryProperty.get() >> directory
        5 * directory.getAsFile() >> file
        5 * file.getAbsolutePath() >> "/apth"
        2 * javaTestCompileTask.getClasspath() >> fileCollection
        2 * fileCollection.getFiles() >> fileSet
        4 * javaTestCompileTask.getSource() >> fileTree
        4 * fileTree.getFiles() >> fileSet

        2 * project.getLayout() >> projectLayout
        1 * projectLayout.getProjectDirectory() >> projectDirectory
        1 * projectDirectory.getAsFile() >> realFile
        1 * projectLayout.getBuildDirectory() >> directoryProperty
        1 * directoryProperty.getAsFile() >> buildDirectoryProvider
        1 * buildDirectoryProvider.get() >> realFile
    }

    // Successful execution of the invoke method with valid project setup
    def "should execute invoke method successfully with valid project setup"() {
        given:
        def project = Mock(Project)
        def logger = Mock(Logger)
        def modelPrompter = Mock(ModelPrompterImpl)
        def coverAgentExecutor = Mock(CoverAgentExecutor)
        def builder = new CoverAgentBuilder()
                .apiKey("validApiKey")
                .model("validWanDBApiKey")
                .iterations(10)
                .coverage(80)
                .coverAgentBinaryPath("/path/to/binary")
                .project(project)
                .openAiChatModelBuilder(Mock(OpenAiChatModel.OpenAiChatModelBuilder))
                .modelPrompter(modelPrompter)
                .coverAgentExecutor(coverAgentExecutor)
        TaskContainer container = Mock(TaskContainer)
        TaskCollection collection = Mock(TaskCollection)
        JavaCompile javaTestCompileTask = Mock(JavaCompile)
        ConfigurationContainer configurationContainer = Mock(ConfigurationContainer)
        DependencyHandler dependencyHandler = Mock(DependencyHandler)
        Dependency dependency = Mock(Dependency)
        Configuration conf = Mock(Configuration)
        Set<File> files = [new File('src/test/resources/build.gradle')]
        ExecResult execResult = Mock(ExecResult)

        when:
        CoverAgentImpl coverAgent = builder.build()
        coverAgent.javaCompileCommand = Optional.of("src/test/resources/mock.sh")
        coverAgent.javaTestCompileCommand = Optional.of("src/test/resources/mock.sh")
        coverAgent.javaTestSourceFiles.add(new File('src/test/resources/CalcTest.java'))
        coverAgent.invoke()

        then:
        _ * conf.resolve() >> files
        _ * project.getDependencies() >> dependencyHandler
        _ * project.getConfigurations() >> configurationContainer
        _ * dependencyHandler.create(_) >> dependency
        _ * configurationContainer.detachedConfiguration(_) >> conf
        _ * project.getTasks() >> container
        _ * container.withType(JavaCompile.class) >> collection
        _ * collection.findByName(_) >> javaTestCompileTask

        _ * project.getLogger() >> logger
        _ * modelPrompter.chatter(_, _) >> new TestInfoResponse("sourceFilePath")
        _ * project.exec(_) >> execResult
        _ * execResult.getExitValue() >> 1
    }

    def "path detectFramework testImplementation present but no testing"() {
        given:
        Project project = Mock(Project)
        CoverAgentBuilder builder = CoverAgentBuilder.builder().project(project)
        CoverAgentImpl coverAgent = new CoverAgentImpl(builder)
        ConfigurationContainer configurationContainer = Mock(ConfigurationContainer)
        Configuration configuration = Mock(Configuration)
        DependencySet dependencies = Mock(DependencySet)
        Iterator iterator = Mock(Iterator)

        when:
        coverAgent.detectFramework()

        then:
        1 * project.getConfigurations() >> configurationContainer
        1 * configurationContainer.findByName("testImplementation") >> configuration
        _ * configuration.getDependencies() >> dependencies
        _ * dependencies.iterator() >> iterator
        _ * iterator.hasNext() >> false
    }

    def "path detectFramework testCompile present but no testing"() {
        given:
        Project project = Mock(Project)
        CoverAgentBuilder builder = CoverAgentBuilder.builder().project(project)
        CoverAgentImpl coverAgent = new CoverAgentImpl(builder)
        ConfigurationContainer configurationContainer = Mock(ConfigurationContainer)
        Configuration configuration = Mock(Configuration)
        DependencySet dependencies = Mock(DependencySet)
        Iterator iterator = Mock(Iterator)

        when:
        coverAgent.detectFramework()

        then:
        1 * project.getConfigurations() >> configurationContainer
        1 * configurationContainer.findByName("testCompile") >> configuration
        _ * configuration.getDependencies() >> dependencies
        _ * dependencies.iterator() >> iterator
        _ * iterator.hasNext() >> false
    }

    def "Configuration giving different dependencies "() {
        given:
        TestingFramework framework = new TestingFramework(fr_label, group, version, name)
        CoverAgentBuilder builder = CoverAgentBuilder.builder().project(Mock(Project))
        CoverAgentImpl coverAgent = new CoverAgentImpl(builder)
        Configuration configuration = Mock(Configuration)
        DependencySet dependencies = new MockDepencySet()
        Dependency dependency = Mock(Dependency)
        dependencies.dependencies = [dependency]

        when:
        coverAgent.detectFrameworkInConfiguration(configuration)
        coverAgent.testingFrameworks.get(0) == framework

        then:
        1 * configuration.getDependencies() >> dependencies
        1 * dependency.getGroup() >> group
        1 * dependency.getName() >> name
        1 * dependency.getVersion() >> version

        where:
        group               | name     | fr_label          | version
        "org.junit.jupiter" | "Junit5" | "junit"           | "1.2.3"
        "org.junit.vintage" | "Junit4" | "junit"           | "1.0.+"
        "testng"            | "testng" | "TestNG"          | "1.2.3"
        "spock"             | "spock"  | "Spock Framework" | "0.0.2"

    }

}
