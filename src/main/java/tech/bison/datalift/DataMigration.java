package tech.bison.datalift;

import com.commercetools.api.client.ProjectApiRoot;

public interface DataMigration {

  int version();

  void execute(ProjectApiRoot projectApiRoot);
}
