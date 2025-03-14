/*
 * Copyright (C) 2024 Bison Schweiz AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.bison.datalift.core.api.executor;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.google.common.base.Strings;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import tech.bison.datalift.core.api.configuration.CommercetoolsProperties;
import tech.bison.datalift.core.api.configuration.Configuration;
import tech.bison.datalift.core.api.exception.DataLiftException;

public class Context {

  private final Configuration configuration;

  public Context(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * @return The commercetools api root object.
   */
  public ProjectApiRoot getProjectApiRoot() {
    if (configuration.getApiRoot() != null) {
      return configuration.getApiRoot();
    }
    return createProjectApiRoot(configuration.getApiProperties());
  }

  /**
   * @return The commercetools import api root object.
   */
  public com.commercetools.importapi.client.ProjectApiRoot getImportProjectApiRoot() {
    if (configuration.getImportApiRoot() != null) {
      return configuration.getImportApiRoot();
    }
    if (configuration.getImportApiProperties() == null) {
      throw new DataLiftException("Missing commercetools import api properties.");
    }
    return createImportProjectApiRoot(configuration.getImportApiProperties(), configuration.getApiProperties());
  }

  private ProjectApiRoot createProjectApiRoot(CommercetoolsProperties properties) {
    return ApiRootBuilder.of().defaultClient(
            ClientCredentials.of().withClientId(properties.clientId())
                .withClientSecret(properties.clientSecret())
                .build(),
            properties.authUrl(), properties.apiUrl())
        .build(properties.projectKey());
  }

  private com.commercetools.importapi.client.ProjectApiRoot createImportProjectApiRoot(CommercetoolsProperties importProperties, CommercetoolsProperties apiProperties) {
    return com.commercetools.importapi.defaultconfig.ImportApiRootBuilder.of().defaultClient(
            ClientCredentials.of().withClientId(Strings.isNullOrEmpty(importProperties.clientId()) ? apiProperties.clientId() : importProperties.clientId())
                .withClientSecret(Strings.isNullOrEmpty(importProperties.clientSecret()) ? apiProperties.clientSecret() : importProperties.clientSecret())
                .build(),
            Strings.isNullOrEmpty(importProperties.authUrl()) ? apiProperties.authUrl() : importProperties.authUrl(),
            importProperties.apiUrl())
        .build(Strings.isNullOrEmpty(importProperties.projectKey()) ? apiProperties.projectKey() : importProperties.projectKey());
  }
}
