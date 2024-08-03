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
import tech.bison.datalift.core.api.configuration.Configuration;
import tech.bison.datalift.core.api.configuration.FluentConfiguration;
import tech.bison.datalift.core.api.exception.DataLiftException;
import tech.bison.datalift.core.api.executor.Context;
import tech.bison.datalift.core.api.executor.DataLiftExecutor;
import tech.bison.datalift.core.api.migration.DataMigration;
import tech.bison.datalift.core.api.resolver.MigrationResolver;
import tech.bison.datalift.core.api.versioner.VersionInfo;
import tech.bison.datalift.core.api.versioner.Versioner;
import tech.bison.datalift.core.internal.resolver.ClasspathMigrationResolver;
import tech.bison.datalift.core.internal.versioner.CustomObjectBasedVersioner;

/**
 * Entry point for a data migration run.
 */
public class DataLift {

  private static final Logger LOG = LoggerFactory.getLogger(DataLift.class);

  private final Configuration configuration;
  private final Versioner versioner;
  private final MigrationResolver migrationResolver;
  private final DataLiftExecutor dataLiftExecutor;

  /**
   * Constructor for testing
   */
  DataLift(Configuration configuration, Versioner versioner, MigrationResolver migrationResolver, DataLiftExecutor dataLiftExecutor) {
    this.configuration = configuration;
    this.versioner = versioner;
    this.migrationResolver = migrationResolver;
    this.dataLiftExecutor = dataLiftExecutor;
  }

  public DataLift(Configuration configuration) {
    this.configuration = configuration;
    var objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    this.versioner = new CustomObjectBasedVersioner(objectMapper);
    this.migrationResolver = new ClasspathMigrationResolver(configuration.getClasspathFilter());
    this.dataLiftExecutor = new DataLiftExecutor(versioner);
  }

  public static FluentConfiguration configure() {
    return new FluentConfiguration();
  }

  /**
   * Executes the provided data migrations. Previously executed migrations are not run again.
   *
   * @throws DataLiftException in case of any exception thrown during execution.
   */
  public void execute() {
    try {
      var context = createContext();
      final List<DataMigration> foundMigrations = migrationResolver.resolve(context);
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
      dataLiftExecutor.execute(context, version, migrationsToExecute);
    } catch (Exception ex) {
      throw new DataLiftException("Error while executing data migrations.", ex);
    }
  }

  private Context createContext() {
    return new Context(configuration);
  }

  private List<DataMigration> getMigrationsToExecute(List<DataMigration> foundMigrations, VersionInfo version) {
    return foundMigrations.stream()
        .filter(m -> m.version() > version.current())
        .sorted(comparingInt(DataMigration::version))
        .toList();
  }
}
