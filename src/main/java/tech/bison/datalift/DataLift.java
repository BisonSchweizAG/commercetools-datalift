package tech.bison.datalift;

import static java.util.Comparator.comparingInt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class DataLift {

  private final Versioner versioner;
  private final MigrationLoader migrationLoader;
  private final Runner runner;

  public DataLift(Versioner versioner, MigrationLoader migrationLoader, Runner runner) {
    this.versioner = versioner;
    this.migrationLoader = migrationLoader;
    this.runner = runner;
  }

  public static DataLift createWithDefaults(String classpathFilter) {
    final Versioner versioner = new CustomObjectBasedVersioner(new ObjectMapper());
    final MigrationLoader migrationLoader = new ClasspathMigrationLoader(classpathFilter);
    final Runner runner = new RunnerImpl(versioner);
    return new DataLift(versioner, migrationLoader, runner);
  }

  public void execute(Context context) {
    final List<DataMigration> foundMigrations = migrationLoader.load(context);
    if (foundMigrations.isEmpty()) {
      return;
    }
    final VersionInfo version = versioner.currentVersion(context);

    final List<DataMigration> migrationsToExecute = getMigrationsToExecute(foundMigrations, version);
    if (migrationsToExecute.isEmpty()) {
      return;
    }
    runner.execute(context, version, migrationsToExecute);
  }

  private List<DataMigration> getMigrationsToExecute(List<DataMigration> foundMigrations, VersionInfo version) {
    return foundMigrations.stream()
        .filter(m -> m.version() > version.current())
        .sorted(comparingInt(DataMigration::version))
        .toList();
  }
}
