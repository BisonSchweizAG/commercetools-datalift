package tech.bison.datalift.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ContextCreatorTest {

  @Test
  void createContextInitializesCommercetoolsApiRoot() {
    var context = new ContextCreator().create(new CommercetoolsProperties("clientId", "clientSecret", "http://localhost:8080/", "authUrl", "projectKey"));

    assertEquals("projectKey", context.getProjectApiRoot().getProjectKey());
    assertEquals("http://localhost:8080/", context.getProjectApiRoot().getApiHttpClient().getBaseUri().toString());
  }
}
