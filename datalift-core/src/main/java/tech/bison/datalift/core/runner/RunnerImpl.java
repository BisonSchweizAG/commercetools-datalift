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
package tech.bison.datalift.core.runner;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.bison.datalift.core.Context;
import tech.bison.datalift.core.DataMigration;
import tech.bison.datalift.core.versioner.VersionInfo;
import tech.bison.datalift.core.versioner.Versioner;

public class RunnerImpl implements Runner {

  private static Logger LOG = LoggerFactory.getLogger(RunnerImpl.class);
  private final Versioner versioner;

  public RunnerImpl(Versioner versioner) {
    this.versioner = versioner;
  }

  @Override
  public void execute(Context context, VersionInfo versionInfo, List<DataMigration> migrationsToExecute) {
    VersionInfo currentVersionInfo = versionInfo;
    for (DataMigration migration : migrationsToExecute) {
      int version = migration.version();
      LOG.info("Running data migration {}, version {}.", migration.getClass().getName(), version);
      migration.execute(context.getProjectApiRoot());
      currentVersionInfo = versioner.updateVersion(context, version, currentVersionInfo.documentVersion());
    }
  }
}
