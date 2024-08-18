/*-
 * ========================LICENSE_START=================================
 * datalift
 * ========================================================================
 * Copyright (C) 2024 Bison Schweiz AG
 * ========================================================================
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
 * =========================LICENSE_END==================================
 */
package tech.bison.datalift.commandline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import tech.bison.datalift.core.DataLift;
import tech.bison.datalift.core.api.configuration.CommercetoolsProperties;
import tech.bison.datalift.core.api.exception.DataLiftException;

@Command(
    name = "datalift",
    description = "executes datalift"
)
public class Main implements Runnable, CommandLineArguments {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  @Option(names = {"--clientId"}, required = true)
  private String clientId;
  @Option(names = {"--clientSecret"}, required = true)
  private String clientSecret;
  @Option(names = {"--apiUrl"}, required = true)
  private String apiUrl;
  @Option(names = {"--authUrl"}, required = true)
  private String authUrl;
  @Option(names = {"--importClientId"})
  private String importClientId;
  @Option(names = {"--importClientSecret"})
  private String importClientSecret;
  @Option(names = {"--importApiUrl"})
  private String importApiUrl;
  @Option(names = {"--importAuthUrl"})
  private String importAuthUrl;
  @Option(names = {"--projectKey"}, required = true)
  private String projectKey;
  @Option(names = {"--locations"}, required = false, description = "A comma seperated list of packages to scan")
  private String locations;


  public static void main(String[] args) {
    CommandLine cmd = new CommandLine(new Main());
    int exitCode = cmd.execute(args);
    System.exit(exitCode);
  }

  @Override
  public void run() {
    LOG.info("Now we would execute DataLift with [{}][{}}][{}}]", apiUrl, authUrl, projectKey);
    try {
      var dataLift = new ConfigurationManager().getConfiguration(this).load();
      dataLift.execute();
    } catch (DataLiftException ex) {
      LOG.error("An error occurred while executing data migrations.", ex);
    }
    LOG.info("DataLift end");
  }

  @Override
  public String getLocations() {
    return locations;
  }

  @Override
  public String getClientId() {
    return clientId;
  }

  @Override
  public String getClientSecret() {
    return clientSecret;
  }

  @Override
  public String getApiUrl() {
    return apiUrl;
  }

  @Override
  public String getAuthUrl() {
    return authUrl;
  }

  @Override
  public String getImportClientId() {
    return importClientId;
  }

  @Override
  public String getImportClientSecret() {
    return importClientSecret;
  }

  @Override
  public String getImportApiUrl() {
    return importApiUrl;
  }

  @Override
  public String getImportAuthUrl() {
    return importAuthUrl;
  }

  @Override
  public String getProjectKey() {
    return projectKey;
  }
}
