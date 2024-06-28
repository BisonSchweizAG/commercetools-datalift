package tech.bison.datalift.core;

public class DataLiftException extends RuntimeException {

  DataLiftException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
