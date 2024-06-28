package tech.bison.datalift;

record VersionInfo(int current, Long documentVersion) {

  static VersionInfo initialVersion() {
    return new VersionInfo(0, null);
  }
}
