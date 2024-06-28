package tech.bison.datalift;

import java.util.List;

interface Runner {

  void execute(Context context, VersionInfo versionInfo, List<DataMigration> migrationsToExecute);
}
