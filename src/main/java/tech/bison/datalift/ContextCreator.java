package tech.bison.datalift;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;

class ContextCreator {

  Context create(CommercetoolsProperties commercetoolsProperties) {
    return new Context(createProjectApiRoot(commercetoolsProperties));
  }

  private ProjectApiRoot createProjectApiRoot(CommercetoolsProperties properties) {
    return ApiRootBuilder.of().defaultClient(
            ClientCredentials.of().withClientId(properties.clientId())
                .withClientSecret(properties.clientSecret())
                .build(),
            properties.authUrl(), properties.apiUrl())
        .build(properties.projectKey());
  }
}
