/*
 * Copyright (C) 2000 - 2024 Bison Schweiz AG
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
package tech.bison.datalift.core.api.configuration;

import static java.util.stream.Collectors.toList;

import com.commercetools.api.client.ProjectApiRoot;
import java.util.Arrays;
import java.util.List;
import tech.bison.datalift.core.DataLift;
import tech.bison.datalift.core.api.Location;
import tech.bison.datalift.core.api.exception.DataLiftException;


public class FluentConfiguration implements Configuration {

  private List<String> locations;
  private ProjectApiRoot projectApiRoot;
  private CommercetoolsProperties apiProperties;
  private CommercetoolsProperties importApiProperties;
  private com.commercetools.importapi.client.ProjectApiRoot importApiRoot;

  public FluentConfiguration() {
    withLocations("data.migration");
  }

  /**
   * @return The new fully-configured Datalift instance.
   */
  public DataLift load() {
    validateConfiguration();
    return new DataLift(this);
  }

  private void validateConfiguration() {
    if (projectApiRoot == null && apiProperties == null) {
      throw new DataLiftException("Missing commercetools import api configuration. Either use withApiProperties() or withApiRoot().");
    }
  }

  /**
   * Configures the locations to scan recursively for migrations. Must point to a valid package name on the classpath
   *
   * @param locations Locations to scan recursively for migrations. (default: data.migration)
   */
  public FluentConfiguration withLocations(String... locations) {
    this.locations = Arrays.stream(locations).collect(toList());
    return this;
  }

  /**
   * Configure the commercetools api with properties.
   */
  public FluentConfiguration withApiProperties(CommercetoolsProperties apiProperties) {
    this.apiProperties = apiProperties;
    return this;
  }

  /**
   * Configure the commercetools import api with properties.
   */
  public FluentConfiguration withImportApiProperties(CommercetoolsProperties importApiProperties) {
    this.importApiProperties = importApiProperties;
    return this;
  }

  /**
   * Configure the commercetools api with the given api root.
   */
  public FluentConfiguration withApiRoot(ProjectApiRoot projectApiRoot) {
    this.projectApiRoot = projectApiRoot;
    return this;
  }

  /**
   * Configure the commercetools api with the given import api root.
   */
  public FluentConfiguration withImportApiRoot(com.commercetools.importapi.client.ProjectApiRoot importApiRoot) {
    this.importApiRoot = importApiRoot;
    return this;
  }

  @Override
  public ProjectApiRoot getApiRoot() {
    return projectApiRoot;
  }

  @Override
  public CommercetoolsProperties getApiProperties() {
    return apiProperties;
  }

  @Override
  public CommercetoolsProperties getImportApiProperties() {
    return importApiProperties;
  }

  @Override
  public com.commercetools.importapi.client.ProjectApiRoot getImportApiRoot() {
    return importApiRoot;
  }

  @Override
  public List<Location> getLocations() {
    return locations.stream().map(Location::new).toList();
  }
}
