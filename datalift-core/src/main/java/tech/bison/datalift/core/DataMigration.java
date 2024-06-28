package tech.bison.datalift.core;

import com.commercetools.api.client.ProjectApiRoot;

public interface DataMigration {

  int version();

  void execute(ProjectApiRoot projectApiRoot);
}
