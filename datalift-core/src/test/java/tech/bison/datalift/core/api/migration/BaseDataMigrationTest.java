package tech.bison.datalift.core.api.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import tech.bison.datalift.core.api.exception.DataLiftException;
import tech.bison.datalift.core.api.executor.Context;

class BaseDataMigrationTest {

  @Test
  void init_noPrefixFoundInClassName_throwException() {
    var exception = assertThrows(DataLiftException.class, InvalidDataMigration::new);
    assertEquals("Invalid class name: InvalidDataMigration. Ensure it starts with V or implement tech.bison.datalift.core.DataMigration directly for non-default naming", exception.getMessage());
  }

  @Test
  void init_validName_parseVersionAndDescription() {
    var dataMigration = new V1__Data_Migration_with_description();

    assertEquals(1, dataMigration.version());
    assertEquals("Data Migration with description", dataMigration.description());
  }

}

class V1__Data_Migration_with_description extends BaseDataMigration {

  @Override
  public void execute(Context context) {

  }
}

class InvalidDataMigration extends BaseDataMigration {

  @Override
  public void execute(Context context) {

  }
}
