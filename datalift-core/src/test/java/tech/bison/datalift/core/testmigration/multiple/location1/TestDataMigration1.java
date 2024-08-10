package tech.bison.datalift.core.testmigration.multiple.location1;

import tech.bison.datalift.core.api.executor.Context;
import tech.bison.datalift.core.api.migration.DataMigration;

public class TestDataMigration1 implements DataMigration {

  @Override
  public int version() {
    return 1;
  }

  @Override
  public String description() {
    return "TestDataMigration1";
  }

  @Override
  public void execute(Context context) {

  }
}
