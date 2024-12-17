package ai.qodo.cover.plugin;

public record CoverageReport(String coverageReport, boolean metPercentage, TestClassReport testClassReport,
                             String source) {
}
