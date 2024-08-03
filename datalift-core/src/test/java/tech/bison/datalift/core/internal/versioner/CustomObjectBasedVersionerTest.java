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
package tech.bison.datalift.core.internal.versioner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.custom_object.CustomObjectDraft;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vrap.rmf.base.client.error.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.bison.datalift.core.api.configuration.Configuration;
import tech.bison.datalift.core.api.executor.Context;

@ExtendWith(MockitoExtension.class)
class CustomObjectBasedVersionerTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProjectApiRoot projectApiRoot;
  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private Configuration configuration;

  @BeforeEach
  void setup() {
    when(configuration.getApiRoot()).thenReturn(projectApiRoot);
  }

  @Test
  void customObjectExistsThenReturnCurrentVersion() {
    var existingVersionDto = new VersionInfoDto(10);
    var customObject = createCustomObject(existingVersionDto, 1L);

    when(projectApiRoot.customObjects()
        .withContainerAndKey("version", "versionKey")
        .get()
        .executeBlocking()
        .getBody())
        .thenReturn(customObject);
    when(objectMapper.convertValue(customObject.getValue(), VersionInfoDto.class)).thenReturn(existingVersionDto);

    var versionInfo = createVersioner().currentVersion(createContext());

    assertEquals(10, versionInfo.current());
  }


  @Test
  void customObjectNotExistsThenReturnZeroVersion() {
    when(projectApiRoot.customObjects()
        .withContainerAndKey("version", "versionKey")
        .get()
        .executeBlocking()
        .getBody()).thenThrow(NotFoundException.class);

    var versionInfo = createVersioner().currentVersion(createContext());

    assertEquals(0, versionInfo.current());
  }

  @Test
  void updateVersionThenCreateOrUpdateCustomObject() {
    var updatedVersionDto = new VersionInfoDto(20);
    var customObject = createCustomObject(updatedVersionDto, 2L);
    when(projectApiRoot.customObjects()
        .post(Mockito.any(CustomObjectDraft.class))
        .executeBlocking()
        .getBody()).thenReturn(customObject);

    var versionInfo = createVersioner().updateVersion(createContext(), 20, 1L);

    assertEquals(20, versionInfo.current());
    assertEquals(2, versionInfo.documentVersion());
  }

  private Context createContext() {
    return new Context(configuration);
  }

  private CustomObject createCustomObject(VersionInfoDto versionInfoDto, Long documentVersion) {
    var customObject = mock(CustomObject.class);
    lenient().when(customObject.getValue()).thenReturn(versionInfoDto);
    lenient().when(customObject.getVersion()).thenReturn(documentVersion);
    return customObject;
  }

  private CustomObjectBasedVersioner createVersioner() {
    return new CustomObjectBasedVersioner(objectMapper);
  }
}
