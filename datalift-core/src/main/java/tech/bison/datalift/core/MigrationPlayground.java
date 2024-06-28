/*-
 * ========================LICENSE_START=================================
 * datalift
 * ========================================================================
 * Copyright (C) 2010 - 2024 Red Gate Software Ltd
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
package tech.bison.datalift.core;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.customer.AuthenticationMode;
import com.commercetools.api.models.customer.CustomerDraft;

public class MigrationPlayground implements DataMigration {

  @Override
  public int version() {
    return 4;
  }

  @Override
  public void execute(ProjectApiRoot projectApiRoot) {
    System.out.println("Executing playground migration.");
    projectApiRoot.customers().post(CustomerDraft.builder().firstName("Datalift2").authenticationMode(AuthenticationMode.EXTERNAL_AUTH).email("hans.muster1@test.com").build()).executeBlocking();
  }
}
