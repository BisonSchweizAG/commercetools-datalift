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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.commercetools.importapi.client.ProjectApiRoot;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import tech.bison.datalift.core.api.configuration.CommercetoolsProperties;
import tech.bison.datalift.core.api.configuration.FluentConfiguration;
import tech.bison.datalift.core.api.exception.DataLiftException;

@RunWith(MockitoJUnitRunner.class)
class ContextTest {

  @Test
  void getImportApiRoot_importApiPropertiesExists_createImportApiRoot() {
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
    verify(importProperties).authUrl();
    verify(importProperties).clientId();
    verify(importProperties).clientSecret();
    verify(importProperties).projectKey();
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
    return new FluentConfiguration()
        .withApiProperties(new CommercetoolsProperties("clientId", "clientSecret", "aApiUr",
            "authUrl", "projectKey"));
  }
}
