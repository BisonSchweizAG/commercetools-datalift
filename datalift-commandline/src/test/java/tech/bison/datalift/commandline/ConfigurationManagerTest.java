package tech.bison.datalift.commandline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import tech.bison.datalift.core.api.configuration.CommercetoolsProperties;

class ConfigurationManagerTest {

  @Test
  void getConfiguration_noLocations_returnConfiguration() {
    var commandLineArguments = mock(CommandLineArguments.class);
    when(commandLineArguments.getApiUrl()).thenReturn("apiUrl");
    when(commandLineArguments.getAuthUrl()).thenReturn("authUrl");
    when(commandLineArguments.getClientId()).thenReturn("clientId");
    when(commandLineArguments.getClientSecret()).thenReturn("clientSecret");
    when(commandLineArguments.getImportApiUrl()).thenReturn("importApiUrl");
    when(commandLineArguments.getImportClientId()).thenReturn("importClientId");
    when(commandLineArguments.getImportClientSecret()).thenReturn("importClientSecret");

    var configuration = createConfigurationManager().getConfiguration(commandLineArguments);

    assertApiProperties(configuration.getApiProperties());
    assertImportApiProperties(configuration.getImportApiProperties());
    assertEquals(1, configuration.getLocations().size());
  }

  @Test
  void getConfiguration_singleLocation_setLocation() {
    var commandLineArguments = mock(CommandLineArguments.class);
    when(commandLineArguments.getLocations()).thenReturn("location1");

    var configuration = createConfigurationManager().getConfiguration(commandLineArguments);

    assertEquals(1, configuration.getLocations().size());
    assertEquals("location1", configuration.getLocations().get(0).path());
  }

  @Test
  void getConfiguration_multipleLocation_parseLocations() {
    var commandLineArguments = mock(CommandLineArguments.class);
    when(commandLineArguments.getLocations()).thenReturn("location1,location2");

    var configuration = createConfigurationManager().getConfiguration(commandLineArguments);

    assertEquals(2, configuration.getLocations().size());
    assertEquals("location1", configuration.getLocations().get(0).path());
    assertEquals("location2", configuration.getLocations().get(1).path());
  }

  @Test
  void getConfiguration_importApiUrlNull_returnConfiguration() {
    var commandLineArguments = mock(CommandLineArguments.class);
    when(commandLineArguments.getApiUrl()).thenReturn("apiUrl");
    when(commandLineArguments.getAuthUrl()).thenReturn("authUrl");
    when(commandLineArguments.getClientId()).thenReturn("clientId");
    when(commandLineArguments.getClientSecret()).thenReturn("clientSecret");

    var configuration = createConfigurationManager().getConfiguration(commandLineArguments);

    assertNull(configuration.getImportApiProperties());
  }

  @Test
  void getConfiguration_importApiUrlEmpty_returnConfiguration() {
    var commandLineArguments = mock(CommandLineArguments.class);
    when(commandLineArguments.getApiUrl()).thenReturn("apiUrl");
    when(commandLineArguments.getAuthUrl()).thenReturn("authUrl");
    when(commandLineArguments.getClientId()).thenReturn("clientId");
    when(commandLineArguments.getClientSecret()).thenReturn("clientSecret");
    when(commandLineArguments.getImportApiUrl()).thenReturn("");

    var configuration = createConfigurationManager().getConfiguration(commandLineArguments);

    assertNull(configuration.getImportApiProperties());
  }

  private void assertImportApiProperties(CommercetoolsProperties properties) {
    assertEquals(properties.apiUrl(), "importApiUrl");
    assertEquals(properties.clientId(), "importClientId");
    assertEquals(properties.clientSecret(), "importClientSecret");
  }

  private void assertApiProperties(CommercetoolsProperties properties) {
    assertEquals(properties.apiUrl(), "apiUrl");
    assertEquals(properties.authUrl(), "authUrl");
    assertEquals(properties.clientId(), "clientId");
    assertEquals(properties.clientSecret(), "clientSecret");
  }

  private ConfigurationManager createConfigurationManager() {
    return new ConfigurationManager();
  }

}
