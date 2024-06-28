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

import java.util.List;
import org.reflections.Reflections;
import tech.bison.datalift.core.Context;
import tech.bison.datalift.core.DataMigration;

public class ClasspathMigrationLoader implements MigrationLoader {

  private final String classpathFilter;

  public ClasspathMigrationLoader(String classpathFilter) {
    this.classpathFilter = classpathFilter;
  }

  @Override
  public List<DataMigration> load(Context context) {
    Reflections reflections = new Reflections(classpathFilter);

    return reflections.getSubTypesOf(DataMigration.class).stream()
        .map(clazz -> {
          try {
            return (DataMigration) clazz.getDeclaredConstructor().newInstance();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        })
        .toList();
  }
}
