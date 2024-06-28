package tech.bison.datalift;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "datalift",
    description = "executes datalift"
)
public class Main implements Runnable {

  @Option(names = {"--clientId"}, required = true)
  private String clientId;
  @Option(names = {"--clientSecret"}, required = true)
  private String clientSecret;
  @Option(names = {"--apiUrl"}, required = true)
  private String apiUrl;
  @Option(names = {"--authUrl"}, required = true)
  private String authUrl;
  @Option(names = {"--projectKey"}, required = true)
  private String projectKey;
  @Option(names = {"--packageFilter"}, required = true, description = "Package filter")
  private String packageFilter;

  public static void main(String[] args) {
    CommandLine.run(new Main(), args);
  }

  @Override
  public void run() {
    System.out.println("Now we would execute DataLift with [" + clientId + "][" + clientSecret + "][" + apiUrl + "][" + authUrl + "][" + projectKey + "]");
    final var context = new ContextCreator().create(new CommercetoolsProperties(clientId, clientSecret, apiUrl, authUrl, projectKey));
    final String classpathFilter = getClasspathFilter();
    DataLift.createWithDefaults(classpathFilter).execute(context);
    System.out.println("DataLift end");
  }

  private String getClasspathFilter() {
    if (packageFilter == null || packageFilter.length() <= 1) {
      throw new IllegalArgumentException("PackageFilter must be defined");
    }
    return packageFilter;
  }
}
