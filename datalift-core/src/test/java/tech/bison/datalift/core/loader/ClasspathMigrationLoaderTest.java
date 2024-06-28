package tech.bison.datalift.core.loader;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.bison.datalift.core.Context;
import tech.bison.datalift.core.testmigration.TestDataMigration;

class ClasspathMigrationLoaderTest {

  @Test
  void migrationExistsInPackageThenReturnMigration() {
    var context = Mockito.mock(Context.class);
    var classpathMigrationLoader = new ClasspathMigrationLoader("tech.bison.datalift.core.testmigration");

    var result = classpathMigrationLoader.load(context);

    assertEquals(1, result.size());
    assertEquals(TestDataMigration.class, result.getFirst().getClass());
  }

  @Test
  void noMigrationExistsInPackageThenReturnNoMigration() {
    var context = mock(Context.class);
    var classpathMigrationLoader = new ClasspathMigrationLoader("tech.bison.datalift.foo");

    var result = classpathMigrationLoader.load(context);

    assertEquals(0, result.size());
  }
}
