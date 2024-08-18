package tech.bison.datalift.core.testmigration.valid.sub;

import tech.bison.datalift.core.api.executor.Context;
import tech.bison.datalift.core.api.migration.DataMigration;

public class TestDataMigration implements DataMigration {

  @Override
  public int version() {
    return 1;
  }

  @Override
  public String description() {
    return "TestDataMigration";
  }

  @Override
  public void execute(Context context) {

  }
}
