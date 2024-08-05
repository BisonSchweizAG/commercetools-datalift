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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.commercetools.api.client.ProjectApiRoot;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.bison.datalift.core.loader.MigrationLoader;
import tech.bison.datalift.core.runner.Runner;
import tech.bison.datalift.core.versioner.VersionInfo;
import tech.bison.datalift.core.versioner.Versioner;

@ExtendWith(MockitoExtension.class)
class DataLiftTest {

  @Mock
  private Versioner versioner;
  @Mock
  private MigrationLoader migrationLoader;
  @Mock
  private Runner runner;

  @Captor
  private ArgumentCaptor<List<DataMigration>> migrations;

  @Test
  void exceptionOnLoadThenThrowDataLiftException() {
    Context context = null;
    var innerException = new IllegalStateException();
    when(migrationLoader.load(context)).thenThrow(innerException);
    DataLift dataLift = new DataLift(versioner, migrationLoader, runner);

    var exception = assertThrows(DataLiftException.class, () -> dataLift.execute(context));
    assertEquals("Error while executing data migrations.", exception.getMessage());
    assertEquals(innerException, exception.getCause());
  }

  @Test
  void noMigrationsResultInNoAction() {
    Context context = null;
    when(migrationLoader.load(context)).thenReturn(List.of());
    DataLift dataLift = new DataLift(versioner, migrationLoader, runner);

    dataLift.execute(context);

    verifyNoInteractions(versioner);
    verifyNoInteractions(runner);
  }


  @ParameterizedTest
  @MethodSource("testDataForNoAction")
  void migrationsResultInNoAction(MigrationsToExecute data) {
    final List<DataMigration> migrations = data.migrations.stream().map(v -> fakeMigration(v)).toList();
    final Context context = null;
    when(migrationLoader.load(context)).thenReturn(migrations);
    final VersionInfo versionInfo = createVersionInfo(data.currentVersion());
    when(versioner.currentVersion(context)).thenReturn(versionInfo);
    DataLift dataLift = new DataLift(versioner, migrationLoader, runner);

    dataLift.execute(context);

    verifyNoInteractions(runner);
  }

  static Stream<MigrationsToExecute> testDataForNoAction() {
    return Stream.of(
        new MigrationsToExecute(List.of(21, 22), 23),
        new MigrationsToExecute(List.of(17, 23), 23));
  }

  @ParameterizedTest
  @MethodSource("testDataForAction")
  void migrationsToExecuteResultInAction(MigrationsNoAction data) {
    final List<DataMigration> migrations = data.migrations.stream().map(v -> fakeMigration(v)).toList();
    final int currentVersion = data.current();
    final List<Integer> migrationsToExecute = data.toExecute();
    final Context context = null;
    when(migrationLoader.load(context)).thenReturn(migrations);
    final VersionInfo versionInfo = createVersionInfo(currentVersion);
    when(versioner.currentVersion(context)).thenReturn(versionInfo);
    DataLift dataLift = new DataLift(versioner, migrationLoader, runner);

    dataLift.execute(context);

    verify(runner).execute(eq(context), eq(versionInfo), this.migrations.capture());
    assertThat(this.migrations.getValue())
        .extracting(m -> m.version())
        .containsExactly(migrationsToExecute.toArray(new Integer[]{}));
  }

  static Stream<MigrationsNoAction> testDataForAction() {
    return Stream.of(
        new MigrationsNoAction(List.of(1, 2, 3, 4), 0, List.of(1, 2, 3, 4)),
        new MigrationsNoAction(List.of(1, 2, 3, 4), 1, List.of(2, 3, 4)),
        new MigrationsNoAction(List.of(1, 2, 3, 4), 2, List.of(3, 4)),
        new MigrationsNoAction(List.of(4, 3, 2, 1), 0, List.of(1, 2, 3, 4)),
        new MigrationsNoAction(List.of(3, 2, 4, 1), 1, List.of(2, 3, 4)),
        new MigrationsNoAction(List.of(1, 4, 3, 2), 2, List.of(3, 4))
    );
  }

  private VersionInfo createVersionInfo(int version) {
    return new VersionInfo(version, Long.valueOf(version));
  }

  private static DataMigration fakeMigration(int migrationVersion) {
    return new DataMigration() {
      @Override
      public int version() {
        return migrationVersion;
      }

      @Override
      public String description() {
        return "";
      }

      @Override
      public void execute(ProjectApiRoot projectApiRoot) {

      }
    };
  }

  private record MigrationsToExecute(List<Integer> migrations, int currentVersion) {

  }

  private record MigrationsNoAction(List<Integer> migrations, int current, List<Integer> toExecute) {

  }
}
