package tech.bison.datalift;

import java.util.List;

public class DataLift {

  private final Versioner versioner;
  private final ScriptLoader scriptLoader;
  private final Runner runner;

  public DataLift(Versioner versioner, ScriptLoader scriptLoader, Runner runner) {
    this.versioner = versioner;
    this.scriptLoader = scriptLoader;
    this.runner = runner;
  }

  public void execute(Context context) {
    final List<DataMigration> foundMigrations = scriptLoader.load(context);
    if(foundMigrations.isEmpty()) {
      return;
    }
    final VersionInfo version = versioner.currentVersion(context);
    final List<DataMigration> migrationsToExecute = getMigrationsToExecute(foundMigrations, version);
    runner.execute(context, migrationsToExecute);
  }

  private List<DataMigration> getMigrationsToExecute(List<DataMigration> foundMigrations, VersionInfo version) {
    return null;
  }
}
