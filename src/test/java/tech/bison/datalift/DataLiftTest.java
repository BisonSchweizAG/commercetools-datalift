package tech.bison.datalift;

import static org.assertj.core.api.Assertions.assertThat;
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
    return () -> migrationVersion;
  }

  private record MigrationsToExecute(List<Integer> migrations, int currentVersion) {

  }

  private record MigrationsNoAction(List<Integer> migrations, int current, List<Integer> toExecute) {

  }
}
