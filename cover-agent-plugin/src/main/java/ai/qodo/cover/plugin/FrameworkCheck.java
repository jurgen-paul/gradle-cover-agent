package ai.qodo.cover.plugin;

import org.gradle.api.artifacts.Dependency;

public class FrameworkCheck {
  private final String identifier;
  private final boolean checkVersion;

  FrameworkCheck(String identifier) {
    this(identifier, false);
  }

  FrameworkCheck(String identifier, boolean checkVersion) {
    this.identifier = identifier;
    this.checkVersion = checkVersion;
  }

  boolean matches(Dependency dependency) {
    if (checkVersion) {
      return dependency.getVersion() != null && dependency.getVersion().startsWith(identifier);
    }
    return dependency.getGroup() != null && dependency.getGroup().contains(identifier);
  }

  String getFramework() {
    return identifier;
  }

}
