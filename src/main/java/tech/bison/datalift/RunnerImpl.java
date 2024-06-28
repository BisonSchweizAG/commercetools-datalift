package tech.bison.datalift;

import java.util.List;

public class RunnerImpl implements Runner{

  private final Versioner versioner;

  public RunnerImpl(Versioner versioner) {
    this.versioner = versioner;
  }

  @Override
  public void execute(Context context, VersionInfo versionInfo, List<DataMigration> migrationsToExecute) {
    VersionInfo currentVersionInfo = versionInfo;
    for(DataMigration migration: migrationsToExecute) {
      migration.execute(context.getProjectApiRoot());
      currentVersionInfo = versioner.updateVersion(context, migration.version(), currentVersionInfo.documentVersion());
    }
  }
}
