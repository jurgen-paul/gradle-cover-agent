package ai.qodo.cover.plugin;

import java.util.List;

public record NewTest(String testBehavior,
                      List<Integer> linesCovered,
                      String methodSignatureFromSource,
                      List<Integer> lineNumbersOfSource,
                      List<String> newImportsForTestClass,
                      String newTestMethod,
                      String inDepthDescription) {
}
