package tech.bison.datalift;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataliftTest {

  @Mock
  private Versioner versioner;
  @Mock
  private ScriptLoader scriptLoader;
  @Mock
  private Runner runner;

  @Test
  void noMigrationsResultInNoAction() {
    Context context = null;
    when(scriptLoader.load(context)).thenReturn(List.of());
    DataLift dataLift = new DataLift(versioner, scriptLoader, runner);

    dataLift.execute(context);

    verifyNoInteractions(versioner);
    verifyNoInteractions(runner);
  }

  @Test
  void migrationsAlreadyExecutedResultInNoAction() {
    Context context = null;
    DataMigration migration = new DataMigration() {
      @Override
      public int version() {
        return 22;
      }
    };
    when(scriptLoader.load(context)).thenReturn(List.of(migration));
    final VersionInfo versionInfo = new VersionInfo(23);
    when(versioner.currentVersion(context)).thenReturn(versionInfo);
    DataLift dataLift = new DataLift(versioner, scriptLoader, runner);

    dataLift.execute(context);

    verifyNoInteractions(runner);
  }
}
