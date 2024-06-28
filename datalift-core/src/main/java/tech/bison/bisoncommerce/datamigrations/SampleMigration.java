package tech.bison.bisoncommerce.datamigrations;

import com.commercetools.api.client.ProjectApiRoot;
import tech.bison.datalift.core.DataMigration;

public class SampleMigration implements DataMigration {

  @Override
  public int version() {
    return 6;
  }

  @Override
  public void execute(ProjectApiRoot projectApiRoot) {
    throw new IllegalStateException("Error in sample migration.");
  }
}
