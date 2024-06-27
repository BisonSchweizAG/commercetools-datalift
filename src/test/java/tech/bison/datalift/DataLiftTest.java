package tech.bison.datalift;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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

  @Test
  void noMigrationsResultInNoAction() {
    Context context = null;
    when(MigrationLoader.load(context)).thenReturn(List.of());
    DataLift dataLift = new DataLift(versioner, scriptLoader, runner);

    dataLift.execute(context);

    verifyNoInteractions(versioner);
    verifyNoInteractions(runner);
  }


  @ParameterizedTest
  @MethodSource("testDataForNoAction")
  void migrationsResultInNoAction(MigratonTestData data) {
    final Context context = null;
    when(scriptLoader.load(context)).thenReturn(data.migrations());
    final VersionInfo versionInfo = new VersionInfo(data.currentVersion());
    when(versioner.currentVersion(context)).thenReturn(versionInfo);
    DataLift dataLift = new DataLift(versioner, scriptLoader, runner);

    dataLift.execute(context);

    verifyNoInteractions(runner);
  }

  static Stream<MigratonTestData> testDataForNoAction() {
    return Stream.of(
        new MigratonTestData(
            List.of(fakeMigration(22)),
            23
        ),
        new MigratonTestData(
            List.of(fakeMigration(23)),
            23
        )
    );
  }

  @Test
  void migration24Current23ResultInRun24() {
    final Context context = null;
    final DataMigration migration = fakeMigration(24);
    when(scriptLoader.load(context)).thenReturn(List.of(migration));
    final VersionInfo versionInfo = new VersionInfo(23);
    when(versioner.currentVersion(context)).thenReturn(versionInfo);
    DataLift dataLift = new DataLift(versioner, scriptLoader, runner);

    dataLift.execute(context);

    verify(runner).execute(context, List.of(migration));
  }

  @Test
  void migration20To24Current23ResultInRun24() {
    final Context context = null;
    final DataMigration migration21 = fakeMigration(21);
    final DataMigration migration22 = fakeMigration(22);
    final DataMigration migration23 = fakeMigration(23);
    final DataMigration migration24 = fakeMigration(24);
    when(scriptLoader.load(context)).thenReturn(List.of(migration21, migration22, migration23, migration24));
    final VersionInfo versionInfo = new VersionInfo(23);
    when(versioner.currentVersion(context)).thenReturn(versionInfo);
    DataLift dataLift = new DataLift(versioner, scriptLoader, runner);

    dataLift.execute(context);

    verify(runner).execute(context, List.of(migration24));
  }

  private static DataMigration fakeMigration(int migrationVersion) {
    return () -> migrationVersion;
  }

  private record MigratonTestData(List<DataMigration> migrations, int currentVersion) {

  }
}
