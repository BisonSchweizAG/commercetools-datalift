/*-
 * ========================LICENSE_START=================================
 * datalift
 * ========================================================================
 * Copyright (C) 2010 - 2024 Red Gate Software Ltd
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
import tech.bison.datalift.core.CommercetoolsProperties;
import tech.bison.datalift.core.ContextCreator;
import tech.bison.datalift.core.DataLift;
import tech.bison.datalift.core.DataLiftException;

@Command(
    name = "datalift",
    description = "executes datalift"
)
public class Main implements Runnable {

  private static Logger LOG = LoggerFactory.getLogger(Main.class);

  @Option(names = {"--clientId"}, required = true)
  private String clientId;
  @Option(names = {"--clientSecret"}, required = true)
  private String clientSecret;
  @Option(names = {"--apiUrl"}, required = true)
  private String apiUrl;
  @Option(names = {"--authUrl"}, required = true)
  private String authUrl;
  @Option(names = {"--projectKey"}, required = true)
  private String projectKey;
  @Option(names = {"--packageFilter"}, required = true, description = "Package filter")
  private String packageFilter;


  public static void main(String[] args) {
    CommandLine cmd = new CommandLine(new Main());
    int exitCode = cmd.execute(args);
    System.exit(exitCode);
  }

  @Override
  public void run() {
    LOG.info("Now we would execute DataLift with [{}][{}}][{}}]", apiUrl, authUrl, projectKey);
    final var context = new ContextCreator().create(new CommercetoolsProperties(clientId, clientSecret, apiUrl, authUrl, projectKey));
    final String classpathFilter = getClasspathFilter();
    try {
      DataLift.createWithDefaults(classpathFilter).execute(context);
    } catch (DataLiftException ex) {
      LOG.error("An error occurred while executing data migrations.", ex);
    }
    LOG.info("DataLift end");
  }

  private String getClasspathFilter() {
    if (packageFilter == null || packageFilter.length() <= 1) {
      throw new IllegalArgumentException("PackageFilter must be defined");
    }
    return packageFilter;
  }
}
