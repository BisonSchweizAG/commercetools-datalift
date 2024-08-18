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

package tech.bison.datalift.commandline;

import tech.bison.datalift.core.DataLift;
import tech.bison.datalift.core.api.configuration.CommercetoolsProperties;
import tech.bison.datalift.core.api.configuration.Configuration;
import tech.bison.datalift.core.api.configuration.FluentConfiguration;

public class ConfigurationManager {

  public FluentConfiguration getConfiguration(CommandLineArguments commandLineArguments) {
    var fluentConfiguration =  DataLift.configure()
        .withApiProperties(new CommercetoolsProperties(
            commandLineArguments.getClientId(),
            commandLineArguments.getClientSecret(),
            commandLineArguments.getApiUrl(),
            commandLineArguments.getAuthUrl(),
            commandLineArguments.getProjectKey()))
        .withImportApiProperties(new CommercetoolsProperties(
            commandLineArguments.getImportClientId(),
            commandLineArguments.getImportClientSecret(),
            commandLineArguments.getImportApiUrl(),
            commandLineArguments.getImportAuthUrl(),
            commandLineArguments.getProjectKey()));

    if (commandLineArguments.getLocations() != null) {
      fluentConfiguration.withLocations(getLocations(commandLineArguments.getLocations()));
    }
    return fluentConfiguration;
  }

  private String[] getLocations(String locations) {
    return locations.split(",");
  }
}
