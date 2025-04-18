/*
 * Copyright (C) 2000 - 2025 Bison Schweiz AG
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

package tech.bison.datalift.core.internal.util;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ResourceInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import tech.bison.datalift.core.api.exception.DataLiftException;

public class ResourceUtils {

  private ResourceUtils() {
    // static access only
  }

  public static List<byte[]> getResourceInfos(final String resourceFolderPath, final Class<?> clazz) {
    List<byte[]> resources = new ArrayList<>();
    try {
      final String resourcePath;
      if (isRelativePath(resourceFolderPath)) {
        resourcePath = String.format("%s/%s", convertPackageNameToResourcePath(clazz.getPackage().getName()), resourceFolderPath);
      } else {
        resourcePath = StringUtils.removeStart(resourceFolderPath, "/");
      }
      var resourceInfos = ClassPath.from(clazz.getClassLoader()).getResources().stream()
          .filter(r -> r.getResourceName().startsWith(resourcePath))
          .toList();
      for (ResourceInfo resourceInfo : resourceInfos) {
        resources.add(resourceInfo.asByteSource().read());
      }
      return resources;
    } catch (IOException e) {
      throw new DataLiftException("Unable to read files from resource folder.", e);
    }
  }

  private static String convertPackageNameToResourcePath(String packageName) {
    return packageName.replace(".", "/");
  }

  private static boolean isRelativePath(String resourceFolderPath) {
    return !resourceFolderPath.startsWith("/");
  }
}
