package tech.bison.datalift;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vrap.rmf.base.client.error.NotFoundException;

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
      var response = context.getProjectApiRoot().customObjects()
          .withContainerAndKey(VERSION_CONTAINER, VERSION_OBJECT_KEY).get()
          .executeBlocking();
      var version = objectMapper.convertValue(response.getBody().getValue(), VersionInfoDto.class);
      return new VersionInfo(version.current());
    } catch (NotFoundException ex) {
      return new VersionInfo(0);
    }
  }
}
