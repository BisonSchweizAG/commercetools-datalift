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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.bison.datalift.core.DataLift;
import tech.bison.datalift.core.api.configuration.CommercetoolsProperties;
import tech.bison.datalift.core.api.configuration.FluentConfiguration;
import tech.bison.datalift.core.api.exception.DataLiftException;
import tech.bison.datalift.core.internal.util.ClassUtils;

public class ConfigurationManager {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationManager.class);
  public static final String DEFAULT_CLI_JARS_LOCATION = "jars";

  public FluentConfiguration getConfiguration(CommandLineArguments commandLineArguments) {
    var installDir = ClassUtils.getInstallDir(Main.class);
    var fluentConfiguration = DataLift.configure(getClassLoaderWithJars(installDir))
        .withApiProperties(new CommercetoolsProperties(
            commandLineArguments.getClientId(),
            commandLineArguments.getClientSecret(),
            commandLineArguments.getApiUrl(),
            commandLineArguments.getAuthUrl(),
            commandLineArguments.getProjectKey()));
    if (commandLineArguments.getImportApiUrl() != null && !commandLineArguments.getImportApiUrl().isBlank()) {
      fluentConfiguration = fluentConfiguration.withImportApiProperties(new CommercetoolsProperties(
          commandLineArguments.getImportClientId(),
          commandLineArguments.getImportClientSecret(),
          commandLineArguments.getImportApiUrl(),
          commandLineArguments.getImportAuthUrl(),
          commandLineArguments.getProjectKey()));
    }

    if (commandLineArguments.getLocations() != null) {
      fluentConfiguration.withLocations(getLocations(commandLineArguments.getLocations()));
    }
    return fluentConfiguration;
  }

  private String[] getLocations(String locations) {
    return locations.split(",");
  }

  private static ClassLoader getClassLoaderWithJars(String workingDirectory) {
    List<String> jarDirs = new ArrayList<>();
    File jarDir = new File(workingDirectory, DEFAULT_CLI_JARS_LOCATION);
    if (jarDir.exists()) {

      jarDirs.add(jarDir.getAbsolutePath());
    }
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    List<File> jarFiles = new ArrayList<>(getJarFiles(jarDirs.toArray(new String[0])));
    if (!jarFiles.isEmpty()) {
      LOG.info("JARs directory found. Loading data migrations from {} jar files.", jarFiles.size());
      classLoader = ClassUtils.addJarsOrDirectoriesToClasspath(classLoader, jarFiles);
    }
    return classLoader;
  }

  private static List<File> getJarFiles(String[] dirs) {
    if (dirs.length == 0) {
      return Collections.emptyList();
    }

    List<File> jarFiles = new ArrayList<>();
    for (String dirName : dirs) {
      File dir = new File(dirName);
      File[] files = dir.listFiles((dir1, name) -> name.endsWith(".jar"));

      if (files == null) {
        throw new DataLiftException("Directory for Data Migrations not found: " + dirName);
      }

      jarFiles.addAll(Arrays.asList(files));
    }

    return jarFiles;
  }
}
