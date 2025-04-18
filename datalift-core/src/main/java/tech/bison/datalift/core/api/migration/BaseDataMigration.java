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
package tech.bison.datalift.core.api.migration;

import io.vrap.rmf.base.client.utils.json.JsonUtils;
import java.util.List;
import tech.bison.datalift.core.api.exception.DataLiftException;
import tech.bison.datalift.core.api.executor.Context;
import tech.bison.datalift.core.internal.util.ResourceUtils;

public abstract class BaseDataMigration implements DataMigration {

  private final static String MIGRATION_INFO_SEPARATOR = "__";
  private int version;
  private String description;

  /**
   * Creates a new instance of a Java-based migration following datalift's default naming convention.
   */
  public BaseDataMigration() {
    init();
  }

  protected void init() {
    extractVersionAndDescription();
  }

  protected void extractVersionAndDescription() {
    String shortName = getClass().getSimpleName();
    String prefix = null;

    if (shortName.startsWith("V")) {
      prefix = shortName.substring(0, 1);
    }
    if (prefix == null) {
      throw new DataLiftException("Invalid class name: " + shortName +
          ". Ensure it starts with V or implement tech.bison.datalift.core.DataMigration directly for non-default naming");
    }
    int separatorPos = shortName.indexOf(MIGRATION_INFO_SEPARATOR);
    if (separatorPos < 0) {
      throw new DataLiftException("Wrong version migration name format: " + shortName
          + ". It must contain a version and should look like this: V{version}__{description}");
    } else {
      try {
        version = Integer.parseInt(shortName.substring(1, separatorPos));
        description = shortName.substring(separatorPos + 1).replace("_", " ").trim();
      } catch (NumberFormatException e) {
        throw new DataLiftException("Wrong version migration name format: " + shortName
            + " (could not recognise version number " + version + ")", e);
      }
    }
  }

  /**
   * Reads a UTF-8 JSON text file from the classpath of the current thread and transforms it into a Java object.
   * <p>
   * An absolute resource name is constructed from the given resource name using the same algorithm as Class.getResourceAsStream()
   *
   * @param name  the name of the resource.
   *              <p>
   *              Example: If the data migration class file is located in a package data.migration and the file is located under "src/main/resources/data/migration/myCustomObject.json" then the path should be "myCustomObject.json"
   * @param clazz the class of the result
   * @param <T>   the type of the result
   * @return the created objected
   */
  protected <T> T readJsonFromResource(String name, Class<T> clazz) {
    return JsonUtils.fromInputStream(getClass().getResourceAsStream(name), clazz);
  }

  /**
   * Reads all UTF-8 JSON text files from a folder of the classpath of the current thread and transforms it into a Java object.
   * <p>
   *
   * @param resourceFolderPath the path to the resource. The path can be relative to the data migration package or an absolute path.
   *                           <p>
   *                           Example: If the data migration class file is located in a package data.migration and the folder is located in "src/main/resources/data/migration/jsonFiles" then the path should be "jsonFiles"
   * @param clazz              the class of the result
   * @param <T>                the type of the result
   * @return the created objected
   */
  protected <T> List<T> readJsonFromResourceFolder(String resourceFolderPath, Class<T> clazz) {
    return ResourceUtils.getResourceInfos(resourceFolderPath, getClass()).stream()
        .map(resource -> JsonUtils.fromJsonByteArray(resource, clazz))
        .toList();
  }

  @Override
  public int version() {
    return version;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public abstract void execute(Context context);
}
