/*
 * Copyright (C) 2000 - 2024 Bison Schweiz AG
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.bison.datalift.core.api.executor;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.commercetools.importapi.client.ProjectApiRoot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.bison.datalift.core.api.configuration.CommercetoolsProperties;
import tech.bison.datalift.core.api.configuration.FluentConfiguration;
import tech.bison.datalift.core.api.exception.DataLiftException;

@ExtendWith(MockitoExtension.class)
class ContextTest {

  @Test
  void getImportApiRoot_allImportApiPropertiesExists_createImportApiRoot() {
    var importProperties = mock(CommercetoolsProperties.class);
    when(importProperties.apiUrl()).thenReturn("http://someapi.com");
    when(importProperties.authUrl()).thenReturn("http://auth.someapi.com");
    when(importProperties.clientId()).thenReturn("impClientId");
    when(importProperties.clientSecret()).thenReturn("impClientSecret");
    when(importProperties.projectKey()).thenReturn("impProjectKey");
    FluentConfiguration configuration = createConfiguration()
        .withImportApiProperties(importProperties);
    var context = new Context(configuration);

    assertNotNull(context.getImportProjectApiRoot());
    verify(importProperties).apiUrl();
    verify(importProperties, Mockito.times(2)).authUrl();
    verify(importProperties, Mockito.times(2)).clientId();
    verify(importProperties, Mockito.times(2)).clientSecret();
    verify(importProperties, Mockito.times(2)).projectKey();
  }

  @Test
  void getImportApiRoot_requiredImportApiPropertiesExists_createImportApiRoot() {
    var importProperties = mock(CommercetoolsProperties.class);
    when(importProperties.apiUrl()).thenReturn("http://imp.someapi.com");

    var apiProperties = mock(CommercetoolsProperties.class);
    when(apiProperties.apiUrl()).thenReturn("http://someapi.com");
    when(apiProperties.authUrl()).thenReturn("http://auth.someapi.com");
    when(apiProperties.clientId()).thenReturn("clientId");
    when(apiProperties.clientSecret()).thenReturn("clientSecret");
    when(apiProperties.projectKey()).thenReturn("projectKey");
    FluentConfiguration configuration = createConfiguration(apiProperties)
        .withImportApiProperties(importProperties);
    var context = new Context(configuration);

    assertNotNull(context.getImportProjectApiRoot());
    verify(importProperties).apiUrl();
    verify(apiProperties).authUrl();
    verify(apiProperties).clientId();
    verify(apiProperties).clientSecret();
    verify(apiProperties).projectKey();
  }

  @Test
  void getImportApiRoot_importApiRootObjectExists_returnImportApiRoot() {
    var importApiRoot = mock(ProjectApiRoot.class);
    FluentConfiguration configuration = createConfiguration()
        .withImportApiRoot(importApiRoot);
    var context = new Context(configuration);

    assertEquals(importApiRoot, context.getImportProjectApiRoot());
  }

  @Test
  void getImportApiRoot_importApiNotConfigured_throwException() {
    var context = new Context(createConfiguration());

    assertThrows(DataLiftException.class, context::getImportProjectApiRoot);
  }

  private static FluentConfiguration createConfiguration() {
    return createConfiguration(new CommercetoolsProperties("clientId", "clientSecret", "aApiUr",
        "authUrl", "projectKey"));
  }

  private static FluentConfiguration createConfiguration(CommercetoolsProperties apiProperties) {
    return new FluentConfiguration()
        .withApiProperties(apiProperties);
  }
}
