package tech.bison.datalift.core;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.bison.datalift.core.api.configuration.CommercetoolsProperties;
import tech.bison.datalift.core.api.configuration.FluentConfiguration;
import tech.bison.datalift.core.api.executor.Context;
import tech.bison.datalift.core.api.versioner.Versioner;
import tech.bison.datalift.core.internal.versioner.CustomObjectBasedVersioner;
import tools.jackson.databind.json.JsonMapper;

@WireMockTest(httpPort = 8087)
class DataLiftIntegrationTest {

  private Context context;
  private Versioner versioner;

  @BeforeEach
  void setUp() {
    var configuration = new FluentConfiguration().withApiProperties(new CommercetoolsProperties("test", "test", "http://localhost:8087", "http://localhost:8087/auth", "integrationtest"));
    stubFor(post(urlEqualTo("/auth"))
        .willReturn(aResponse().withBodyFile("token.json")));
    context = new Context(configuration);
    versioner = new CustomObjectBasedVersioner(JsonMapper.builder().build());
  }

  @Test
  void noCurrentVersionFoundReturnsInitialVersion() {
    stubFor(get(urlPathEqualTo("/integrationtest/custom-objects/version/versionKey"))
        .willReturn(aResponse().withStatus(404).withBodyFile("not-found.json")));

    var versionInfo = versioner.currentVersion(context);

    assertThat(versionInfo).isNotNull();
    assertThat(versionInfo.current()).isEqualTo(0);
  }

  @Test
  void currentVersionOneReturnsVersion() {
    stubFor(get(urlPathEqualTo("/integrationtest/custom-objects/version/versionKey"))
        .willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("get-custom-object.json")));

    var versionInfo = versioner.currentVersion(context);

    assertThat(versionInfo).isNotNull();
    assertThat(versionInfo.current()).isEqualTo(1);
  }
}
