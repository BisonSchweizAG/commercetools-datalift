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
