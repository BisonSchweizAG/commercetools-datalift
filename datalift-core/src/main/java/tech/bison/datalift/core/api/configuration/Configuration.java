/*
 * Copyright (C) 2000 - 2024 Bison Schweiz AG
 *
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
 */
package tech.bison.datalift.core.api.configuration;

import com.commercetools.api.client.ProjectApiRoot;
import java.util.List;
import tech.bison.datalift.core.api.Location;

public interface Configuration {

  List<Location> getLocations();

  ProjectApiRoot getApiRoot();

  CommercetoolsProperties getApiProperties();

  CommercetoolsProperties getImportApiProperties();

  com.commercetools.importapi.client.ProjectApiRoot getImportApiRoot();
}
