package tech.bison.datalift;

/**
 * Responsible to load and update version info in the data store
 */
interface Versioner {

  /**
   * Loads the current data migration version info from the data store
   *
   * @param context
   * @return current version info
   */
  VersionInfo currentVersion(Context context);

  /**
   * Updates the data migration version in the data store
   *
   * @param context
   * @param newVersion
   * @param documentVersion identifier for optimistic locking
   * @return the updated version info
   */
  VersionInfo updateVersion(Context context, int newVersion, Long documentVersion);
}
