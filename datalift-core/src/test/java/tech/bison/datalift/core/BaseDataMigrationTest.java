package tech.bison.datalift.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.commercetools.api.client.ProjectApiRoot;
import org.junit.jupiter.api.Test;

class BaseDataMigrationTest {

  @Test
  void init_noPrefixFoundInClassName_throwException() {
    var exception = assertThrows(DataLiftException.class, InvalidDataMigration::new);
    assertEquals("Invalid class name: InvalidDataMigration. Ensure it starts with V or implement tech.bison.datalift.core.DataMigration directly for non-default naming", exception.getMessage());
  }

  @Test
  void init_validName_parseVersionAndDescription() {
    var dataMigration = new V1_Data_Migration_with_description();

    assertEquals(1, dataMigration.version());
    assertEquals("Data Migration with description", dataMigration.description());
  }

}

class V1_Data_Migration_with_description extends BaseDataMigration {

  @Override
  public void execute(ProjectApiRoot projectApiRoot) {

  }
}

class InvalidDataMigration extends BaseDataMigration {

  @Override
  public void execute(ProjectApiRoot projectApiRoot) {

  }
}
