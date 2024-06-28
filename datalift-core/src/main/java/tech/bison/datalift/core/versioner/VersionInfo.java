package tech.bison.datalift.core.versioner;

public record VersionInfo(int current, Long documentVersion) {

  static VersionInfo initialVersion() {
    return new VersionInfo(0, null);
  }
}
