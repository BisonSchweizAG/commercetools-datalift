package tech.bison.datalift;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataliftTest {

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

  @Test
  void migration22Current23ResultInNoAction() {
    final Context context = null;
    final DataMigration migration = fakeMigration(22);
    when(scriptLoader.load(context)).thenReturn(List.of(migration));
    final VersionInfo versionInfo = new VersionInfo(23);
    when(versioner.currentVersion(context)).thenReturn(versionInfo);
    DataLift dataLift = new DataLift(versioner, scriptLoader, runner);

    dataLift.execute(context);

    verifyNoInteractions(runner);
  }

  @Test
  void migration23Current23ResultInNoAction() {
    final Context context = null;
    final DataMigration migration = fakeMigration(23);
    when(scriptLoader.load(context)).thenReturn(List.of(migration));
    final VersionInfo versionInfo = new VersionInfo(23);
    when(versioner.currentVersion(context)).thenReturn(versionInfo);
    DataLift dataLift = new DataLift(versioner, migrationLoader, runner);

    dataLift.execute(context);

    verifyNoInteractions(runner);
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

  private static DataMigration fakeMigration(int migrationVersion) {
    return () -> migrationVersion;
  }
}
