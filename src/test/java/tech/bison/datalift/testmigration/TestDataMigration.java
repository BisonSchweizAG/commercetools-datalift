package tech.bison.datalift.testmigration;

import tech.bison.datalift.DataMigration;

public class TestDataMigration implements DataMigration {

  @Override
  public int version() {
    return 1;
  }
}
