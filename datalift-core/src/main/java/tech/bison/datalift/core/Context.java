package tech.bison.datalift.core;

import com.commercetools.api.client.ProjectApiRoot;

public class Context {

  private final ProjectApiRoot projectApiRoot;

  public Context(ProjectApiRoot projectApiRoot) {
    this.projectApiRoot = projectApiRoot;
  }

  public ProjectApiRoot getProjectApiRoot() {
    return projectApiRoot;
  }
}
