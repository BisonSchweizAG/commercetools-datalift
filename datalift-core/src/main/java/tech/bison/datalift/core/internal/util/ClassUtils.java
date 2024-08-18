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

package tech.bison.datalift.core.internal.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import tech.bison.datalift.core.api.exception.DataLiftException;

public class ClassUtils {

  private ClassUtils() {
    // static access only
  }

  public static ClassLoader addJarsOrDirectoriesToClasspath(ClassLoader classLoader, List<File> jarFiles) {
    List<URL> urls = new ArrayList<>();
    for (File jarFile : jarFiles) {
      try {
        urls.add(jarFile.toURI().toURL());
      } catch (Exception e) {
        throw new DataLiftException("Unable to load " + jarFile.getPath(), e);
      }
    }
    return new URLClassLoader(urls.toArray(new URL[0]), classLoader);
  }

  public static String getInstallDir(Class<?> clazz) {
    String path = Objects.requireNonNull(ClassUtils.getLocationOnDisk(clazz));
    return new File(path)    // jar file
        .getParentFile() // lib dir
        .getParentFile() // installation dir
        .getAbsolutePath();
  }

  public static String getLocationOnDisk(Class<?> aClass) {
    ProtectionDomain protectionDomain = aClass.getProtectionDomain();
    if (protectionDomain == null) {
      return null;
    }
    CodeSource codeSource = protectionDomain.getCodeSource();
    if (codeSource == null || codeSource.getLocation() == null) {
      return null;
    }
    return URLDecoder.decode(codeSource.getLocation().getPath(), StandardCharsets.UTF_8);
  }
}
