package tech.bison.datalift;

import java.util.List;

interface ScriptLoader {

  List<DataMigration> load(Context context);
}
