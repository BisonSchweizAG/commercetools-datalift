/*-
 * ========================LICENSE_START=================================
 * datalift
 * ========================================================================
 * Copyright (C) 2000 - 2024 Bison Schweiz AG
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
package tech.bison.datalift.core;

import static java.util.Comparator.comparingInt;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.bison.datalift.core.loader.ClasspathMigrationLoader;
import tech.bison.datalift.core.loader.MigrationLoader;
import tech.bison.datalift.core.runner.Runner;
import tech.bison.datalift.core.runner.RunnerImpl;
import tech.bison.datalift.core.versioner.CustomObjectBasedVersioner;
import tech.bison.datalift.core.versioner.VersionInfo;
import tech.bison.datalift.core.versioner.Versioner;

/**
 * Entry point for a data migration run.
 */
public class DataLift {

  private static Logger LOG = LoggerFactory.getLogger(DataLift.class);

  private final Versioner versioner;
  private final MigrationLoader migrationLoader;
  private final Runner runner;

  public DataLift(Versioner versioner, MigrationLoader migrationLoader, Runner runner) {
    this.versioner = versioner;
    this.migrationLoader = migrationLoader;
    this.runner = runner;
  }

  public static DataLift createWithDefaults(String classpathFilter) {
    var objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    final Versioner versioner = new CustomObjectBasedVersioner(objectMapper);
    final MigrationLoader migrationLoader = new ClasspathMigrationLoader(classpathFilter);
    final Runner runner = new RunnerImpl(versioner);
    return new DataLift(versioner, migrationLoader, runner);
  }

  /**
   * Executes the provided data migrations. Previously executed migrations are not run again.
   *
   * @throws DataLiftException in case of any exception thrown during execution.
   */
  public void execute(Context context) {
    try {
      final List<DataMigration> foundMigrations = migrationLoader.load(context);
      if (foundMigrations.isEmpty()) {
        LOG.info("No data migrations found.");
        return;
      }
      final VersionInfo version = versioner.currentVersion(context);
      LOG.info("Current version is {}.", version.current());
      final List<DataMigration> migrationsToExecute = getMigrationsToExecute(foundMigrations, version);
      if (migrationsToExecute.isEmpty()) {
        LOG.info("Data migrations are on current version. Nothing to execute.");
        return;
      }
      runner.execute(context, version, migrationsToExecute);
    } catch (Exception ex) {
      throw new DataLiftException("Error while executing data migrations.", ex);
    }
  }

  private List<DataMigration> getMigrationsToExecute(List<DataMigration> foundMigrations, VersionInfo version) {
    return foundMigrations.stream()
        .filter(m -> m.version() > version.current())
        .sorted(comparingInt(DataMigration::version))
        .toList();
  }
}
