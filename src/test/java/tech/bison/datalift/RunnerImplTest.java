package tech.bison.datalift;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RunnerImplTest {

  @Mock
  private Versioner versioner;
  @Mock
  private Context context;

  @Test
  void onMigrationTodoWritesNewVersion() {
    final Runner runner = new RunnerImpl(versioner);
    VersionInfo currentVersion = new VersionInfo(0, 23L);
    List<DataMigration> migrations = List.of(fakeMigration(1));

    runner.execute(context, currentVersion, migrations);

    verify(versioner).updateVersion(context, 1, 23L);
  }
  @Test
  void onMultipleMigrationsTodoWritesNewVersionOfEachMigration() {
    final Runner runner = new RunnerImpl(versioner);
    VersionInfo currentVersion = new VersionInfo(0, 23L);
    when(versioner.updateVersion(context, 1, 23L)).thenReturn(new VersionInfo(1, 24L));
    when(versioner.updateVersion(context, 2, 24L)).thenReturn(new VersionInfo(1, 25L));
    List<DataMigration> migrations = List.of(
        fakeMigration(1),
        fakeMigration(2)
        );

    runner.execute(context, currentVersion, migrations);

    verify(versioner).updateVersion(context, 1, 23L);
    verify(versioner).updateVersion(context, 2, 24L);
    verifyNoMoreInteractions(versioner);
  }

  private static @NotNull DataMigration fakeMigration(int version) {
    return new DataMigration() {
      @Override
      public int version() {
        return version;
      }
    };
  }
}
