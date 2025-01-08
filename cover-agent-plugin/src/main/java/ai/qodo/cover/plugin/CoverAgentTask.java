package ai.qodo.cover.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CoverAgentTask extends DefaultTask {
  private static final Logger log = LoggerFactory.getLogger(CoverAgentTask.class);
  private CoverAgent coverAgent;
  private Project project;
  private ModelPrompter modelPrompter;

  public CoverAgentTask() {
    init(getProject());
  }

  private void init(Project project) {
    this.project = project;
  }

  protected void setModelPrompter(ModelPrompter modelPrompter) {
    this.modelPrompter = modelPrompter;
  }


  @TaskAction
  public void performTask() {
    project.getLogger().info("Performing task with {}", modelPrompter);

    CoverAgentBuilder builder = CoverAgentBuilder.builder();
    builder.project(this.project);
    builder.modelPrompter(this.modelPrompter);

    coverAgent = builder.build();
    coverAgent.init();
    coverAgent.invoke();
  }


}
