package dev.jose.healflow_api.exceptions;

public class NotFoundException extends RuntimeException {

  private static final String BASE_MESSAGE = "%s with the %s: %s not found";

  public NotFoundException(String resource, String field, Object value) {
    super(String.format(BASE_MESSAGE, resource, field, value.toString()));
  }
}
