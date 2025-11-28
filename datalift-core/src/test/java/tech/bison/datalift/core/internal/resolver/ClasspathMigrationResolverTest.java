/*-
 * ========================LICENSE_START=================================
 * datalift
 * ========================================================================
 * Copyright (C) 2024 Bison Schweiz AG
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package tech.bison.datalift.core.internal.resolver;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.bison.datalift.core.api.Location;
import tech.bison.datalift.core.api.exception.DataLiftException;
import tech.bison.datalift.core.api.executor.Context;
import tech.bison.datalift.core.testmigration.multiple.location1.TestDataMigration1;
import tech.bison.datalift.core.testmigration.multiple.location2.TestDataMigration2;
import tech.bison.datalift.core.testmigration.valid.V2__Test_DataMigration_with_description;
import tech.bison.datalift.core.testmigration.valid.sub.TestDataMigration;

class ClasspathMigrationResolverTest {

  @Test
  void resolve_migrationExistsInPackage_returnMigration() {
    var context = Mockito.mock(Context.class);
    var classpathMigrationLoader = new ClasspathMigrationResolver(List.of(new Location("tech.bison.datalift.core.testmigration.valid")), getClassLoader());

    var result = classpathMigrationLoader.resolve(context);

    assertEquals(2, result.size());
    assertEquals(TestDataMigration.class, result.get(0).getClass());
    assertEquals(V2__Test_DataMigration_with_description.class, result.get(1).getClass());
  }

  @Test
  void resolve_multipleLocations_returnMigrations() {
    var context = Mockito.mock(Context.class);
    var classpathMigrationLoader = new ClasspathMigrationResolver(List.of(
        new Location("tech.bison.datalift.core.testmigration.multiple.location1"),
        new Location("tech.bison.datalift.core.testmigration.multiple.location2")),
        getClassLoader());

    var result = classpathMigrationLoader.resolve(context);

    assertEquals(2, result.size());
    assertEquals(TestDataMigration1.class, result.get(0).getClass());
    assertEquals(TestDataMigration2.class, result.get(1).getClass());
  }

  @Test
  void resolve_noMigrationExistsInPackage_returnNoMigration() {
    var context = mock(Context.class);
    var classpathMigrationLoader = new ClasspathMigrationResolver(List.of(new Location("tech.bison.datalift.foo")), getClassLoader());

    var result = classpathMigrationLoader.resolve(context);

    assertEquals(0, result.size());
  }

  @Test
  void resolve_invalidMigrationExistsInPackage_throwsException() {
    var context = mock(Context.class);
    var classpathMigrationLoader = new ClasspathMigrationResolver(List.of(new Location("tech.bison.datalift.core.testmigration.invalid")), getClassLoader());

    assertThrows(DataLiftException.class, () -> classpathMigrationLoader.resolve(context));
  }

  private ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }
}
