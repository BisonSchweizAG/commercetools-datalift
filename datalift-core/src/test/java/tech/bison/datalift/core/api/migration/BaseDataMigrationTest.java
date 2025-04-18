package tech.bison.datalift.core.api.migration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

  @Test
  void readJsonFromResource_resourceFileExists_readObjectFromFile() {
    var classLoaderSaved = Thread.currentThread().getContextClassLoader();
    var classLoaderMock = Mockito.mock(ClassLoader.class);
    Thread.currentThread().setContextClassLoader(classLoaderMock);
    try {
      var body = new V1__Data_Migration_with_description().readJsonFromResourcePublicize("test-payload.json", TestPayload.class);
      assertEquals("someKey", body.getKey());
      verifyNoInteractions(classLoaderMock);
    } finally {
      Thread.currentThread().setContextClassLoader(classLoaderSaved);
    }
  }

  @Test
  void readJsonFromResource_absolutePath_readObjectFromFile() {
    var classLoaderSaved = Thread.currentThread().getContextClassLoader();
    var classLoaderMock = Mockito.mock(ClassLoader.class);
    Thread.currentThread().setContextClassLoader(classLoaderMock);
    try {
      var body = new V1__Data_Migration_with_description().readJsonFromResourcePublicize("/tech/bison/datalift/core/api/migration/test-payload.json", TestPayload.class);
      assertEquals("someKey", body.getKey());
      verifyNoInteractions(classLoaderMock);
    } finally {
      Thread.currentThread().setContextClassLoader(classLoaderSaved);
    }
  }

  @Test
  void readJsonFromResourceFolder_absolutePath_readObjectsFromFiles() {
    var classLoaderSaved = Thread.currentThread().getContextClassLoader();
    var classLoaderMock = Mockito.mock(ClassLoader.class);
    Thread.currentThread().setContextClassLoader(classLoaderMock);
    try {
      var payloadList = new V1__Data_Migration_with_description().readJsonFromResourceFolderPublicize("/jsonFilesInRoot", TestPayload.class);
      assertThat(payloadList).containsExactlyInAnyOrder(new TestPayload("someKey1"), new TestPayload("someKey2"));
      verifyNoInteractions(classLoaderMock);
    } finally {
      Thread.currentThread().setContextClassLoader(classLoaderSaved);
    }
  }

  @Test
  void readJsonFromResourceFolder_relativePath_readObjectsFromFiles() {
    var classLoaderSaved = Thread.currentThread().getContextClassLoader();
    var classLoaderMock = Mockito.mock(ClassLoader.class);
    Thread.currentThread().setContextClassLoader(classLoaderMock);
    try {
      var payloadList = new V1__Data_Migration_with_description().readJsonFromResourceFolderPublicize("jsonFiles", TestPayload.class);
      assertThat(payloadList).containsExactlyInAnyOrder(new TestPayload("someKey1"), new TestPayload("someKey2"));
      verifyNoInteractions(classLoaderMock);
    } finally {
      Thread.currentThread().setContextClassLoader(classLoaderSaved);
    }
  }

}

class V1__Data_Migration_with_description extends BaseDataMigration {

  public <T> T readJsonFromResourcePublicize(String resourcePath, Class<T> clazz) {
    return readJsonFromResource(resourcePath, clazz);
  }

  public <T> List<T> readJsonFromResourceFolderPublicize(String resourceFolder, Class<T> clazz) {
    return readJsonFromResourceFolder(resourceFolder, clazz);
  }


  @Override
  public void execute(Context context) {

  }
}

class InvalidDataMigration extends BaseDataMigration {

  @Override
  public void execute(Context context) {

  }


}
