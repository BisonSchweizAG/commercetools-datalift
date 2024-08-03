# Datalift

Datalift is a tool that let you automate your data migrations of your commercetools based e-commerce application. Develop data migrations inside your project and run them at deployment time with datalift.

## Setup

Add the latest Datalift dependency to your project.

## Getting Started

create a data migration class inside your project and use the commercetools project api root object to perform the migration logic.

```java
class V1_fix_Data_Migration_with_description extends BaseDataMigration {

    @Override
    public void execute(Context context) {
        var apiRoot = context.getProjectApiRoot();
        // perform migration logic
    }
}
```

configure Datalift using the Datalift root object and execute the migration

```java
DataLift dataLift = DataLift.configure()
        .withApiProperties(new CommercetoolsProperties("clientId", "clientSecret", "apiUrl", "authUrl", "projectKey"))
        .withClasspathFilter("example.package.name")
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
                --packageFilter=sample.package.filter
```

## Building

There is a possibility to use alternative url to maven central:
create gradle.properties and set for example:
REPO1_URL=https://artifactory.example.com/repo1

## How to contribute

Coming soon.

## License

Licensed under Apache 2.0 
