/*
 * Copyright (C) 2024 Bison Schweiz AG
 *
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
 */
package tech.bison.datalift.core.internal.resolver;

import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.bison.datalift.core.api.Location;
import tech.bison.datalift.core.api.exception.DataLiftException;
import tech.bison.datalift.core.api.executor.Context;
import tech.bison.datalift.core.api.migration.DataMigration;
import tech.bison.datalift.core.api.resolver.MigrationResolver;

public class ClasspathMigrationResolver implements MigrationResolver {

  private static final Logger LOG = LoggerFactory.getLogger(ClasspathMigrationResolver.class);
  private final List<Location> locations;
  private final ClassLoader classLoader;

  public ClasspathMigrationResolver(List<Location> locations, ClassLoader classLoader) {
    this.locations = locations;
    this.classLoader = classLoader;
  }

  @Override
  public List<DataMigration> resolve(Context context) {
    List<DataMigration> migrations = new ArrayList<>();

    for (Location location : locations) {
      migrations.addAll(getMigrations(location.path()));
    }
    return migrations;
  }

  private List<DataMigration> getMigrations(String location) {
    LOG.debug("Loading data migrations from '" + location + "' ...");
    try {
      var dataMigrations = ClassPath.from(classLoader)
          .getTopLevelClassesRecursive(location)
          .stream()
          .map(ClassPath.ClassInfo::load)
          .filter(DataMigration.class::isAssignableFrom)
          .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
          .map(clazz -> {
            try {
              return (DataMigration) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
              throw new DataLiftException("Unable to instantiate class " + clazz.getName() + " : " + e.getMessage(), e);
            }
          })
          .sorted(Comparator.comparing(DataMigration::version))
          .toList();
      LOG.debug("Found migrations: " + dataMigrations.size());
      return dataMigrations;
    } catch (IOException e) {
      LOG.warn("Skipping unloadable location: {} ({})", location, e.getMessage());
      return List.of();
    }
  }
}
