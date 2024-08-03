package tech.bison.datalift.core.api.configuration;

import com.commercetools.api.client.ProjectApiRoot;

public interface Configuration {

  String getClasspathFilter();

  ProjectApiRoot getApiRoot();

  CommercetoolsProperties getApiProperties();

  CommercetoolsProperties getImportApiProperties();

  com.commercetools.importapi.client.ProjectApiRoot getImportApiRoot();
}
