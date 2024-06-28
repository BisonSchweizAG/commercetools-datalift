package tech.bison.datalift.core.testmigration;

import com.commercetools.api.client.ProjectApiRoot;
import tech.bison.datalift.core.DataMigration;

public class TestDataMigration implements DataMigration {

  @Override
  public int version() {
    return 1;
  }

  @Override
  public void execute(ProjectApiRoot projectApiRoot) {

  }
}
