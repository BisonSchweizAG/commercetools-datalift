package tech.bison.datalift;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.custom_object.CustomObjectDraft;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vrap.rmf.base.client.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomObjectBasedVersionerTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProjectApiRoot projectApiRoot;
  @Mock
  private ObjectMapper objectMapper;

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
    return new Context(projectApiRoot);
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
