package tech.bison.datalift;

import java.util.List;
import org.reflections.Reflections;

public class ClasspathMigrationLoader implements MigrationLoader {

  private final String classpathFilter;

  public ClasspathMigrationLoader(String classpathFilter) {
    this.classpathFilter = classpathFilter;
  }

  @Override
  public List<DataMigration> load(Context context) {
    Reflections reflections = new Reflections(classpathFilter);

    return reflections.getSubTypesOf(DataMigration.class).stream()
        .map(clazz -> {
          try {
            return (DataMigration) clazz.getDeclaredConstructor().newInstance();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        })
        .toList();
  }
}
