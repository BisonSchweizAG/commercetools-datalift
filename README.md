# Datalift

Datalift is a tool that let you automate your data migrations of your commercetools based e-commerce application. Develop data migrations inside your project and run them at deployment time with datalift.

## Setup

Add the latest Datalift dependency to your project.

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

Now let's configure Datalift using the Datalift root object and execute the migration

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

Instead of executing Datalift from within your application one can use the command line interface. The JAR-files containing the migrations classes must be located in a sub folder named *jars* right beside the bin folder of the installation folder.

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
