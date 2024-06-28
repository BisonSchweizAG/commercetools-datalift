package tech.bison.datalift.core;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.customer.AuthenticationMode;
import com.commercetools.api.models.customer.CustomerDraft;

public class MigrationPlayground implements DataMigration {

  @Override
  public int version() {
    return 4;
  }

  @Override
  public void execute(ProjectApiRoot projectApiRoot) {
    System.out.println("Executing playground migration.");
    projectApiRoot.customers().post(CustomerDraft.builder().firstName("Datalift2").authenticationMode(AuthenticationMode.EXTERNAL_AUTH).email("hans.muster1@test.com").build()).executeBlocking();
  }
}
