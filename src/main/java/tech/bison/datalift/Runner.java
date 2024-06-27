package tech.bison.datalift;

import java.util.List;

interface Runner {

  void execute(Context context, List<DataMigration> migrationsToExecute);
}
