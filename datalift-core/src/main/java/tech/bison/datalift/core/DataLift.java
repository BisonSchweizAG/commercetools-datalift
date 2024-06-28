package tech.bison.datalift.core;

import static java.util.Comparator.comparingInt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import tech.bison.datalift.core.loader.ClasspathMigrationLoader;
import tech.bison.datalift.core.loader.MigrationLoader;
import tech.bison.datalift.core.runner.Runner;
import tech.bison.datalift.core.runner.RunnerImpl;
import tech.bison.datalift.core.versioner.CustomObjectBasedVersioner;
import tech.bison.datalift.core.versioner.VersionInfo;
import tech.bison.datalift.core.versioner.Versioner;

public class DataLift {

  private final Versioner versioner;
  private final MigrationLoader migrationLoader;
  private final Runner runner;

  public DataLift(Versioner versioner, MigrationLoader migrationLoader, Runner runner) {
    this.versioner = versioner;
    this.migrationLoader = migrationLoader;
    this.runner = runner;
  }

  public static DataLift createWithDefaults() {
    final Versioner versioner = new CustomObjectBasedVersioner(new ObjectMapper());
    final MigrationLoader migrationLoader = new ClasspathMigrationLoader("");
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
