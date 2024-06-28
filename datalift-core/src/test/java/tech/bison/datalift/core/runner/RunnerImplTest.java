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
package tech.bison.datalift.core.runner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.commercetools.api.client.ProjectApiRoot;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.bison.datalift.core.Context;
import tech.bison.datalift.core.DataMigration;
import tech.bison.datalift.core.versioner.VersionInfo;
import tech.bison.datalift.core.versioner.Versioner;

@ExtendWith(MockitoExtension.class)
class RunnerImplTest {

  @Mock
  private Versioner versioner;
  @Mock
  private Context context;
  @Mock
  private ProjectApiRoot apiRoot;

  @BeforeEach
  void prepare() {
    when(context.getProjectApiRoot()).thenReturn(apiRoot);
  }

  @Test
  void onMigrationTodoWritesNewVersion() {
    final Runner runner = new RunnerImpl(versioner);
    VersionInfo currentVersion = new VersionInfo(0, 23L);
    List<DataMigration> migrations = List.of(fakeMigration(1));

    runner.execute(context, currentVersion, migrations);

    verify(versioner).updateVersion(context, 1, 23L);
    verify(migrations.getFirst()).execute(apiRoot);
  }

  @Test
  void onMultipleMigrationsTodoWritesNewVersionOfEachMigration() {
    final Runner runner = new RunnerImpl(versioner);
    VersionInfo currentVersion = new VersionInfo(0, 23L);
    when(versioner.updateVersion(context, 1, 23L)).thenReturn(new VersionInfo(1, 24L));
    when(versioner.updateVersion(context, 2, 24L)).thenReturn(new VersionInfo(1, 25L));
    final DataMigration migration1 = fakeMigration(1);
    final DataMigration migration2 = fakeMigration(2);
    final List<DataMigration> migrations = List.of(
        migration1,
        migration2
    );

    runner.execute(context, currentVersion, migrations);

    verify(migration1).execute(apiRoot);
    verify(versioner).updateVersion(context, 1, 23L);
    verify(migration2).execute(apiRoot);
    verify(versioner).updateVersion(context, 2, 24L);
    verifyNoMoreInteractions(migration1);
    verifyNoMoreInteractions(migration2);
    verifyNoMoreInteractions(versioner);
  }

  private static DataMigration fakeMigration(int version) {
    final DataMigration migration = mock(DataMigration.class);
    when(migration.version()).thenReturn(version);
    return migration;
  }
}
