package ai.qodo.cover.plugin;

import ai.qodo.cover.plugin.toml.FileNumberer;
import ai.qodo.cover.plugin.toml.PromptParserImpl;
import org.apache.velocity.VelocityContext;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TestCreatorImpl implements TestCreator {

    private final String sourceFilePath;
    private final String testFilePath;
    private final Project project;
    private final String jacocoCoverageCommand;
    private final String projectPath;
    private final ModelPrompter prompter;
    private final int coveragePercentage;
    private final int iterations;
    private final Logger logger;
    private final String JSON_RESPONSE_SCHEMA_PATH = "/templates/test_response.json";
    private final File coverageReportFile;
    private final LinkedList<CoverageReport> coverageReports = new LinkedList<>();
    private String testFileName;
    private String sourceFileName;
    private File testFile;
    private File sourceFile;
    private String schemaResponse;
    private String className;

    public TestCreatorImpl(String sourceFilePath, String testFilePath, Project project, String jacocoCoverageCommand, String projectPath, ModelPrompter prompter, int coveragePercentage, int iterations, Logger logger, File coverageReportFile) {
        this.logger = logger;
        this.sourceFilePath = sourceFilePath;
        this.testFilePath = testFilePath;
        this.project = project;
        this.jacocoCoverageCommand = jacocoCoverageCommand;
        this.projectPath = projectPath;
        this.prompter = prompter;
        this.iterations = iterations;
        this.coveragePercentage = coveragePercentage;
        this.coverageReportFile = coverageReportFile;
        init();
    }

    private void init() {
        this.testFileName = fileName(testFilePath);
        this.sourceFileName = fileName(sourceFilePath);
        this.testFile = new File(testFilePath);
        this.sourceFile = new File(sourceFilePath);
        StringWriter writer = new StringWriter();
        try (InputStream inputStream = PromptParserImpl.class.getResourceAsStream(JSON_RESPONSE_SCHEMA_PATH)) {
            if (inputStream == null) {
                throw new CoverError("JsonSchema not found: " + JSON_RESPONSE_SCHEMA_PATH);
            }
            int data;
            while ((data = inputStream.read()) != -1) {
                writer.write(data);
            }
            this.schemaResponse = writer.toString().trim();
        } catch (Exception e) {
            logger.error("Could not load jsonSchema", e);
        }
        className = getFullyQualifiedClassName(sourceFile);
    }

    private String loadFile(File file) throws CoverError {
        StringWriter writer = new StringWriter();
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            int data;
            while ((data = inputStream.read()) != -1) {
                writer.write(data);
            }
        } catch (Exception e) {
            logger.error("Failed to read file  " + file, e);
            throw new CoverError("Failed to read the file ", e);
        }
        return writer.toString();
    }

    public boolean execute() throws CoverError {
        String orig = loadFile(testFile);
        Optional<CoverageReport> initialReport = reportOnCoverage(orig);
        if (initialReport.isPresent()) {
            coverageReports.add(initialReport.get());
            if (initialReport.get().metPercentage()) {
                return true;
            } else {
                for (int count = 0; iterations > count; count++) {
                    ModelAskResponse askResponse = askForTests();
                    String updatedTestFile = updateTestFile(askResponse.answer().newTests());
                    Optional<CoverageReport> updatedTest = reportOnCoverage(updatedTestFile);
                    if (updatedTest.isPresent()) {
                        CoverageReport report = updatedTest.get();
                        coverageReports.add(report);
                        if (report.metPercentage()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Optional<CoverageReport> reportOnCoverage(String testSourceFile) throws CoverError {
        Optional<CoverageReport> report = Optional.empty();
        String status = executeCoverage();
        if (!status.equals("FAIL")) {
            if (this.coverageReportFile.exists()) {
                try {
                    String coverageReportContent = Files.readString(coverageReportFile.toPath(), StandardCharsets.UTF_8);
                    // need to do calculation check
                    Map<String, CoverageReport> reports = checkReportScores(coverageReportContent, testSourceFile);
                    CoverageReport coverageReport = reports.get(className);
                    if (coverageReport != null) {
                        report = Optional.of(coverageReport);
                    }
                } catch (IOException e) {
                    logger.error("Error getting coverageReportFile ", e);
                }
            }
        }
        return report;
    }

    public String getFullyQualifiedClassName(File sourceFile) {
        String name = "";
        try {
            List<String> lines = Files.readAllLines(sourceFile.toPath());
            String packageName = "";
            String className = "";

            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("package ")) {
                    packageName = line.substring(8, line.indexOf(';')).trim();
                } else if (line.startsWith("public class ") || line.startsWith("class ")) {
                    className = line.split("\\s+")[2];
                    break;
                }
            }

            //if we wanted the package added name = packageName + "." + className;
            name = className;

        } catch (Exception e) {
            logger.error("Failed to get the fully qualified className", e);
        }
        return name;
    }

    private Map<String, CoverageReport> checkReportScores(String coverageReportContent, String testSource) {
        Map<String, CoverageReport> reports = new HashMap<>();
        String[] lines = coverageReportContent.split("\n");
        String[] header = lines[0].split(",");

        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(",");
            Map<String, String> data = new HashMap<>();
            for (int j = 0; j < header.length; j++) {
                data.put(header[j], values[j]);
            }
            String className = data.get("CLASS");
            int instructionMissed = Integer.parseInt(data.get("INSTRUCTION_MISSED"));
            int instructionCovered = Integer.parseInt(data.get("INSTRUCTION_COVERED"));
            int methodMissed = Integer.parseInt(data.get("METHOD_MISSED"));
            int methodCovered = Integer.parseInt(data.get("METHOD_COVERED"));
            int branchMissed = Integer.parseInt(data.get("BRANCH_MISSED"));
            int branchCovered = Integer.parseInt(data.get("BRANCH_COVERED"));

            int totalInstructions = instructionMissed + instructionCovered;
            int totalMethods = methodMissed + methodCovered;
            int totalBranches = branchMissed + branchCovered;

            double instructionMissedPercentage = totalInstructions > 0 ? (double) instructionMissed / totalInstructions * 100 : 0;
            double methodMissedPercentage = totalMethods > 0 ? (double) methodMissed / totalMethods * 100 : 0;
            double branchMissedPercentage = totalBranches > 0 ? (double) branchMissed / totalBranches * 100 : 0;
            boolean metPercentage = instructionMissedPercentage > coveragePercentage && methodMissedPercentage > coveragePercentage && branchMissedPercentage > coveragePercentage;
            reports.put(className, new CoverageReport(coverageReportContent, metPercentage, new TestClassReport(className, instructionMissedPercentage, methodMissedPercentage, branchMissedPercentage), testSource));
        }
        return reports;
    }

    private String updateTestFile(List<NewTest> tests) {
        String testFileContent = "";
        String updatedContent = null;
        int lastBraceIndex = 1;
        try {
            testFileContent = new String(Files.readAllBytes(Paths.get(testFile.getPath())), StandardCharsets.UTF_8);
            lastBraceIndex = testFileContent.lastIndexOf('}');
            if (lastBraceIndex == -1) {
                logger.error("No closing brace found in the test file.");
                return null;
            }

            // Prepare the new methods to be added
            StringBuilder newMethods = new StringBuilder();
            for (NewTest test : tests) {
                newMethods.append("\n\n").append(test.newTestMethod()).append("\n");
            }

            // Insert the new methods before the last closing brace
            updatedContent = testFileContent.substring(0, lastBraceIndex) + newMethods + testFileContent.substring(lastBraceIndex);

            // Write the updated content back to the file
            Files.write(Paths.get(testFile.getPath()), updatedContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("Error reading or writing the test file", e);
        }
        return updatedContent;
    }

    private ModelAskResponse askForTests() throws CoverError {
        String testFileText = loadFile(this.testFile);
        String sourceFileText = loadFile(this.sourceFile);

        Map<String, Object> keys = Map.of("language", "java", "language_test", "groovy", "source_file_name", sourceFileName, "source_file_numbered", new FileNumberer().addLineNumbers(sourceFileText).trim(), "testing_framework", "spock", "test_file_name", testFileName, "test_file", testFileText.trim(), "code_coverage_report", "", "jsonSchema", schemaResponse, "percentage", this.coveragePercentage + "%");

        VelocityContext context = new VelocityContext(keys);
        return prompter.testChatter(context);
    }


    private String fileName(String filePath) {
        Path path = Paths.get(filePath);
        return path.getFileName().toString();
    }


    private String executeCoverage() throws CoverError {
        ExecResult result = project.exec(getExecAction(jacocoCoverageCommand, projectPath));
        String output = "FAIL";
        if (result.getExitValue() == 0) {
            output = "PASS";
        }
        return output;
    }


    private Action<ExecSpec> getExecAction(String commandString, String projectPath) {
        return (ExecSpec execSpec) -> {
            execSpec.commandLine(commandString);
            execSpec.setWorkingDir(projectPath);
        };
    }
}
