package ch.heigvd;

/**
 * Custom exception class used when a ressource is not not found
 **/
public class NotFoundException extends RuntimeException {
  public NotFoundException(String errorMessage) {
    super(errorMessage);
  }
}
