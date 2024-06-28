package tech.bison.datalift.core.loader;

import java.util.List;
import tech.bison.datalift.core.Context;
import tech.bison.datalift.core.DataMigration;

public interface MigrationLoader {

  List<DataMigration> load(Context context);
}
