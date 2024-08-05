/*-
 * ========================LICENSE_START=================================
 * datalift
 * ========================================================================
 * Copyright (C) 2000 - 2024 Bison Schweiz AG
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
package tech.bison.datalift.core.versioner;

import com.commercetools.api.models.custom_object.CustomObjectDraft;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vrap.rmf.base.client.error.NotFoundException;
import tech.bison.datalift.core.Context;

/**
 * Persists version info in a commercetools custom object.
 */
public class CustomObjectBasedVersioner implements Versioner {

  private final ObjectMapper objectMapper;
  private static final String VERSION_CONTAINER = "version";
  private static final String VERSION_OBJECT_KEY = "versionKey";

  public CustomObjectBasedVersioner(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public VersionInfo currentVersion(Context context) {
    try {
      var responseBody = context.getProjectApiRoot().customObjects()
          .withContainerAndKey(VERSION_CONTAINER, VERSION_OBJECT_KEY).get()
          .executeBlocking()
          .getBody();
      var version = objectMapper.convertValue(responseBody.getValue(), VersionInfoDto.class);
      return new VersionInfo(version.current(), responseBody.getVersion());
    } catch (NotFoundException ex) {
      return VersionInfo.initialVersion();
    }
  }

  @Override
  public VersionInfo updateVersion(Context context, int newVersion, Long documentVersion) {
    var versionInfoDto = new VersionInfoDto(newVersion);
    var draft = CustomObjectDraft.builder()
        .container(VERSION_CONTAINER)
        .key(VERSION_OBJECT_KEY)
        .version(documentVersion)
        .value(versionInfoDto)
        .build();
    var updateResponse = context.getProjectApiRoot().customObjects().post(draft).executeBlocking();
    return new VersionInfo(newVersion, updateResponse.getBody().getVersion());
  }
}
