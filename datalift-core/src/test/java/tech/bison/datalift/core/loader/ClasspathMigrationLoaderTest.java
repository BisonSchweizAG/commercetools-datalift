/*-
 * ========================LICENSE_START=================================
 * datalift
 * ========================================================================
 * Copyright (C) 2010 - 2024 Red Gate Software Ltd
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
