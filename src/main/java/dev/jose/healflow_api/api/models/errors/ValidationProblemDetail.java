package dev.jose.healflow_api.api.models.errors;

import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;

@Schema(
    name = "ValidationProblemDetail",
    description = "RFC 7807 Problem Detail with validation error extensions")
public class ValidationProblemDetail extends ApiProblemDetail {

  private static final String INVALID_IDS_KEY = "invalidIds";

  @Schema(
      description = "List of invalid user IDs that failed validation",
      example =
          "[\"550e8400-e29b-41d4-a716-446655440000\", \"6ba7b810-9dad-11d1-80b4-00c04fd430c8\"]")
  public List<UUID> getInvalidIds() {
    Object value = getProperties() != null ? getProperties().get(INVALID_IDS_KEY) : null;
    if (value instanceof List<?>) {
      @SuppressWarnings("unchecked")
      List<UUID> ids = (List<UUID>) value;
      return ids;
    }
    return null;
  }

  public ValidationProblemDetail setInvalidIds(List<UUID> invalidIds) {
    this.setProperty(INVALID_IDS_KEY, invalidIds);
    return this;
  }

  protected ValidationProblemDetail() {
    super();
  }

  protected ValidationProblemDetail(int status) {
    super(status);
  }

  public static ValidationProblemDetail forStatusAndDetail(HttpStatus status, String detail) {
    ValidationProblemDetail problem = new ValidationProblemDetail(status.value());
    problem.setDetail(detail);
    return problem;
  }

  public static ValidationProblemDetail forStatus(HttpStatus status) {
    return new ValidationProblemDetail(status.value());
  }

  @Override
  public ValidationProblemDetail withTitle(String title) {
    this.setTitle(title);
    return this;
  }

  @Override
  public ValidationProblemDetail withType(URI type) {
    this.setType(type);
    return this;
  }

  @Override
  public ValidationProblemDetail withInstance(URI instance) {
    this.setInstance(instance);
    return this;
  }
}
