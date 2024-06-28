package tech.bison.datalift.core.runner;

import java.util.List;
import tech.bison.datalift.core.Context;
import tech.bison.datalift.core.DataMigration;
import tech.bison.datalift.core.versioner.VersionInfo;
import tech.bison.datalift.core.versioner.Versioner;

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
