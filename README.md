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

## CLI based execution

Instead of executing Datalift from within your application one can use the cli

```shell
java -cp "datalift-commandline.jar;your-fat-jar.jar" tech.bison.datalift.commandline.Main 
                --apiUrl=https://api.europe-west1.gcp.commercetools.com/ 
                --authUrl=https://auth.europe-west1.gcp.commercetools.com/ 
                --clientId=someClientId 
                --clientSecret=someClientSecret 
                --projectKey=someProjetKey
```

## Building

There is a possibility to use alternative url to maven central:
create gradle.properties and set for example:
REPO1_URL=https://artifactory.example.com/repo1

## How to contribute

Coming soon.

## License

Licensed under Apache 2.0 
