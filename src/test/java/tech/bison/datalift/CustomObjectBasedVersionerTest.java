package tech.bison.datalift;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.custom_object.CustomObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vrap.rmf.base.client.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomObjectBasedVersionerTest {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ProjectApiRoot projectApiRoot;
  @Mock
  private ObjectMapper objectMapper;

  @Test
  void customObjectExistsThenReturnCurrentVersion() {
    var existingVersionDto = new VersionInfoDto(1);
    var customObject = mock(CustomObject.class);
    when(customObject.getValue()).thenReturn(existingVersionDto);

    when(projectApiRoot.customObjects()
        .withContainerAndKey("version", "versionKey")
        .get()
        .executeBlocking()
        .getBody())
        .thenReturn(customObject);
    when(objectMapper.convertValue(customObject.getValue(), VersionInfoDto.class)).thenReturn(existingVersionDto);

    var versionInfo = createVersioner().currentVersion(new Context(projectApiRoot));

    assertEquals(1, versionInfo.current());
  }

  @Test
  void customObjectNotExistsThenReturnZeroVersion() {
    when(projectApiRoot.customObjects()
        .withContainerAndKey("version", "versionKey")
        .get()
        .executeBlocking()
        .getBody()).thenThrow(NotFoundException.class);

    var versionInfo = createVersioner().currentVersion(new Context(projectApiRoot));

    assertEquals(0, versionInfo.current());
  }

  private CustomObjectBasedVersioner createVersioner() {
    return new CustomObjectBasedVersioner(objectMapper);
  }
}
