# commercetools Datalift

commercetools Datalift is a data migration tool for commercetools. Develop data migrations inside your project and run them at deployment time with commercetools Datalift. The project is inspired by [FlywayDB](https://flywaydb.org) which solves the problem for relational databases.

## Setup

Add the latest commercetools-datalift-core dependency to your project.

```groovy
implementation "tech.bison:commercetools-datalift-core:x.y.z"
```

(latest version numbers avaible on [Maven Central](https://central.sonatype.com/search?namespace=tech.bison&name=commercetools-datalift-core))

## Getting Started

Datalift adheres to the following ***naming convention for migration scripts***:  
*\<Version>__\<Description>.java*

So let's create a migration directory *src/main/java/data/migration* inside our project.  
Followed by a data migration class called **V1__Data_Migration_with_description.java**:

```java
package data.migration;

import tech.bison.datalift.core.api.executor.Context;
import tech.bison.datalift.core.api.migration.BaseDataMigration;

class V1__Data_Migration_with_description extends BaseDataMigration {

    @Override
    public void execute(Context context) {
        var apiRoot = context.getProjectApiRoot();
        // perform migration logic
    }
}
```

We use the commercetools project api root object in the migration above to perform the migration logic.

Now let's configure commercetools Datalift using the Datalift root object and execute the migration

```java
DataLift dataLift = DataLift.configure()
        .withApiProperties(new CommercetoolsProperties("clientId", "clientSecret", "apiUrl", "authUrl", "projectKey"))
        .load()
        .execute();
```

## JSON-based migrations

To execute a migration with JSON data we can create a JSON file in the resources folder and refer to it in a normal Java migration. We can deserialize the file manually or use the helper method *readJsonFromResource()* from the base class *BaseDataMigration*:

In the following example a CustomObjectDraft is read from a JSON-file and posted to the custom-objects endpoint:

```java
class V1__MigrationWithJsonData extends BaseDataMigration {

    @Override
    public void execute(Context context) {
        var apiRoot = context.getProjectApiRoot();
        CustomObjectDraft body = readJsonFromResource("data/migration/createMyCustomObject.json", CustomObjectDraft.class);
        apiRoot.customObjects().post(body);
    }
}
```

## CLI based execution

Instead of executing Datalift from within your application one can use the command line interface. The JAR-files containing the migrations classes must be located in a sub folder named *jars* right beside the bin folder of the installation folder.<br>
You can pass the configuration either with command line arguments or with environment variables. If you call the cli without arguments the output will show you all the available parameters.

Example usage:

```shell
bin/datalift-commandline --apiUrl=https://api.europe-west1.gcp.commercetools.com/ 
                --authUrl=https://auth.europe-west1.gcp.commercetools.com/ 
                --clientId=someClientId 
                --clientSecret=someClientSecret 
                --projectKey=someProjetKey
```

## Building

There is a possibility to use alternative url to maven central:
create gradle.properties and set for example:
REPO1_URL=https://artifactory.example.com/repo1

## License

Datalift is published under the Apache License 2.0, see http://www.apache.org/licenses/LICENSE-2.0 for details.
