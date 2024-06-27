package tech.bison.datalift;

import java.util.List;

interface MigrationLoader {

  List<DataMigration> load(Context context);
}
