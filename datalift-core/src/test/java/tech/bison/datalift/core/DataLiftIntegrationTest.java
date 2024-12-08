package tech.bison.datalift.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tech.bison.datalift.core.api.configuration.CommercetoolsProperties;
import tech.bison.datalift.core.api.configuration.FluentConfiguration;
import tech.bison.datalift.core.api.executor.Context;
import tech.bison.datalift.core.api.versioner.Versioner;
import tech.bison.datalift.core.internal.versioner.CustomObjectBasedVersioner;

@Testcontainers
class DataLiftIntegrationTest {

  public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName
      .parse("mockserver/mockserver")
      .withTag("mockserver-" + MockServerClient.class.getPackage().getImplementationVersion());

  @Container
  public MockServerContainer mockServer = new MockServerContainer(MOCKSERVER_IMAGE);

  private Context context;
  private Versioner versioner;
  private MockServerClient mockServerClient;

  @BeforeEach
  void setUp() {
    var mockServerHostAndPort = "http://" + mockServer.getHost() + ":" + mockServer.getServerPort();
    var configuration = new FluentConfiguration().withApiProperties(new CommercetoolsProperties("test", "test", mockServerHostAndPort, mockServerHostAndPort + "/auth", "integrationtest"));
    context = new Context(configuration);
    versioner = new CustomObjectBasedVersioner(new ObjectMapper());

    mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    mockServerClient
        .when(request().withPath("/auth"))
        .respond(response().withBody(readResponseFromFile("./responses/token.json")));
  }

  @AfterEach
  void tearDown() {
    mockServerClient.reset();
    mockServerClient.close();
  }

  @Test
  void noCurrentVersionFoundReturnsInitialVersion() {
    mockServerClient
        .when(request().withPath("/integrationtest/custom-objects/version/versionKey"))
        .respond(response()
            .withStatusCode(404)
            .withBody(readResponseFromFile("./responses/not-found.json")));

    var versionInfo = versioner.currentVersion(context);

    assertThat(versionInfo).isNotNull();
    assertThat(versionInfo.current()).isEqualTo(0);
  }

  @Test
  void currentVersionOneReturnsVersion() {
    mockServerClient
        .when(request().withPath("/integrationtest/custom-objects/version/versionKey"))
        .respond(response().withBody(readResponseFromFile("./responses/get-custom-object.json")));

    var versionInfo = versioner.currentVersion(context);

    assertThat(versionInfo).isNotNull();
    assertThat(versionInfo.current()).isEqualTo(1);
  }


  String readResponseFromFile(String filename) {
    try {
      var path = Paths.get(getClass().getClassLoader().getResource(filename).toURI());
      var lines = Files.lines(path);
      String data = lines.collect(Collectors.joining("\n"));
      lines.close();
      return data;
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
