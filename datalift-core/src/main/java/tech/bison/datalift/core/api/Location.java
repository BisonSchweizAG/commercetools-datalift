package tech.bison.datalift.core.api;

/**
 * A location to load migrations from.
 */
public final class Location {

  /**
   * The path part of the location.
   */
  private String path;

  public Location(String descriptor) {
    path = descriptor.trim();

    if (path.startsWith("/")) {
      path = path.substring(1);
    }
    if (path.endsWith("/")) {
      path = path.substring(0, path.length() - 1);
    }
  }

  /**
   * @return The path part of the location.
   */
  public String getPath() {
    return path;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Location location = (Location) o;
    return getPath().equals(location.getPath());
  }

  @Override
  public int hashCode() {
    return getPath().hashCode();
  }

  /**
   * @return The complete location descriptor.
   */
  @Override
  public String toString() {
    return getPath();
  }
}
