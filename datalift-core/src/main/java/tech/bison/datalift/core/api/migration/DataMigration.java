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
package tech.bison.datalift.core.api.migration;

import tech.bison.datalift.core.api.executor.Context;

/**
 * Interface for Java-based Migrations.
 *
 * <p>Migration classes implementing this interface will be
 * automatically discovered when placed in the configured location on the classpath.</p>
 *
 * <p>Most users will be better served by subclassing subclass {@link BaseDataMigration} instead of implementing this
 * interface directly, as {@link BaseDataMigration} encourages the use of Datalift's default naming convention and comes with helper methods for JSON-based Migrations.</p>
 */
public interface DataMigration {

  /**
   * @return The version of this data migration. Migrations are executed in ascending order of their versions.
   */
  int version();

  /**
   * @return The description of this migration for the migration history.
   */
  String description();

  void execute(Context context);
}
