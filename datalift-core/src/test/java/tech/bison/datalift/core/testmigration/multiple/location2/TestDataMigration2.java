package tech.bison.datalift.core.testmigration.multiple.location2;

import tech.bison.datalift.core.api.executor.Context;
import tech.bison.datalift.core.api.migration.DataMigration;

public class TestDataMigration2 implements DataMigration {

  @Override
  public int version() {
    return 2;
  }

  @Override
  public String description() {
    return "TestDataMigration2";
  }

  @Override
  public void execute(Context context) {

  }
}
