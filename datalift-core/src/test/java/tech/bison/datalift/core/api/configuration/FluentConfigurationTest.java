package tech.bison.datalift.core.api.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.commercetools.api.client.ProjectApiRoot;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.bison.datalift.core.api.exception.DataLiftException;

class FluentConfigurationTest {

  @Test
  void load_missingApiConfiguration_throwsException() {
    var config = new FluentConfiguration();
    assertThrows(DataLiftException.class, config::load);
  }

  @Test
  void load_validConfiguration_createDataLift() {
    var dataLift = new FluentConfiguration()
        .withApiRoot(Mockito.mock(ProjectApiRoot.class))
        .load();

    assertNotNull(dataLift);
  }
}
