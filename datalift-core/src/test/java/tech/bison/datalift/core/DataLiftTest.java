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
package tech.bison.datalift.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import tech.bison.datalift.core.api.configuration.FluentConfiguration;
import tech.bison.datalift.core.api.exception.DataLiftException;
import tech.bison.datalift.core.api.executor.Context;
import tech.bison.datalift.core.api.executor.DataLiftExecutor;
import tech.bison.datalift.core.api.migration.DataMigration;
import tech.bison.datalift.core.api.resolver.MigrationResolver;
import tech.bison.datalift.core.api.versioner.VersionInfo;
import tech.bison.datalift.core.api.versioner.Versioner;


@ExtendWith(MockitoExtension.class)
class DataLiftTest {

  @Mock
  private Versioner versioner;
  @Mock
  private MigrationResolver migrationResolver;
  @Mock
  private DataLiftExecutor dataLiftExecutor;

  private final FluentConfiguration configuration = new FluentConfiguration();

  @Captor
  private ArgumentCaptor<List<DataMigration>> migrations;

  @Test
  void exceptionOnLoadThenThrowDataLiftException() {
    var innerException = new IllegalStateException();
    when(migrationResolver.resolve(any())).thenThrow(innerException);
    DataLift dataLift = createDataLift();

    var exception = assertThrows(DataLiftException.class, dataLift::execute);
    assertEquals("Error while executing data migrations.", exception.getMessage());
    assertEquals(innerException, exception.getCause());
  }

  @Test
  void noMigrationsResultInNoAction() {
    when(migrationResolver.resolve(any())).thenReturn(List.of());
    DataLift dataLift = createDataLift();

    dataLift.execute();

    verifyNoInteractions(versioner);
    verifyNoInteractions(dataLiftExecutor);
  }


  @ParameterizedTest
  @MethodSource("testDataForNoAction")
  void migrationsResultInNoAction(MigrationsToExecute data) {
    final List<DataMigration> migrations = data.migrations.stream().map(v -> fakeMigration(v)).toList();
    when(migrationResolver.resolve(any())).thenReturn(migrations);
    final VersionInfo versionInfo = createVersionInfo(data.currentVersion());
    when(versioner.currentVersion(any())).thenReturn(versionInfo);
    DataLift dataLift = createDataLift();

    dataLift.execute();

    verifyNoInteractions(dataLiftExecutor);
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
    when(migrationResolver.resolve(any())).thenReturn(migrations);
    final VersionInfo versionInfo = createVersionInfo(currentVersion);
    when(versioner.currentVersion(any())).thenReturn(versionInfo);
    DataLift dataLift = createDataLift();

    dataLift.execute();

    verify(dataLiftExecutor).execute(any(), eq(versionInfo), this.migrations.capture());
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

  private DataLift createDataLift() {
    return new DataLift(configuration, versioner, migrationResolver, dataLiftExecutor);
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
      public void execute(Context context) {

      }
    };
  }

  private record MigrationsToExecute(List<Integer> migrations, int currentVersion) {

  }

  private record MigrationsNoAction(List<Integer> migrations, int current, List<Integer> toExecute) {

  }
}
