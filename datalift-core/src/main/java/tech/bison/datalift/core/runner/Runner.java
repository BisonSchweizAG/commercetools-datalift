package tech.bison.datalift.core.runner;

import java.util.List;
import tech.bison.datalift.core.Context;
import tech.bison.datalift.core.DataMigration;
import tech.bison.datalift.core.versioner.VersionInfo;

public interface Runner {

  void execute(Context context, VersionInfo versionInfo, List<DataMigration> migrationsToExecute);
}
