package ai.qodo.cover.plugin;

import org.apache.velocity.context.Context;

import java.io.File;
import java.util.List;

public interface ModelPrompter {
    TestInfoResponse chatter(List<File> sourceFiles, File testFile) throws CoverError;
    ModelAskResponse testChatter(Context context) throws CoverError;
}
