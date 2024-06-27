package tech.bison.datalift;

import java.util.List;

public class DataLift {

  private final Versioner versioner;
  private final MigrationLoader migrationLoader;
  private final Runner runner;

  public DataLift(Versioner versioner, MigrationLoader migrationLoader, Runner runner) {
    this.versioner = versioner;
    this.migrationLoader = migrationLoader;
    this.runner = runner;
  }

  public void execute(Context context) {
    final List<DataMigration> foundMigrations = migrationLoader.load(context);
    if(foundMigrations.isEmpty()) {
      return;
    }
    final VersionInfo version = versioner.currentVersion(context);
    final List<DataMigration> migrationsToExecute = getMigrationsToExecute(foundMigrations, version);
    if(migrationsToExecute.isEmpty()) {
      return;
    }
    runner.execute(context, migrationsToExecute);
  }

  private List<DataMigration> getMigrationsToExecute(List<DataMigration> foundMigrations, VersionInfo version) {
    return foundMigrations.stream()
        .filter(m -> m.version() > version.current())
        .toList();
  }
}
