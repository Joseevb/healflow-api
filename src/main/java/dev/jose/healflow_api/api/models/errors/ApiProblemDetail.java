package dev.jose.healflow_api.api.models.errors;

import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

@Schema(name = "ApiProblemDetail", description = "RFC 7807 Problem Detail for HTTP APIs")
public class ApiProblemDetail extends ProblemDetail {

  @Schema(
      description = "A URI reference that identifies the problem type",
      example = "https://api.healflow.dev/errors/not-found")
  @Override
  public URI getType() {
    return super.getType();
  }

  @Schema(
      description = "A short, human-readable summary of the problem type",
      example = "Not Found")
  @Override
  public String getTitle() {
    return super.getTitle();
  }

  @Schema(description = "The HTTP status code", example = "404")
  @Override
  public int getStatus() {
    return super.getStatus();
  }

  @Schema(
      description = "A human-readable explanation specific to this occurrence of the problem",
      example = "Appointment with the id: 123 not found")
  @Override
  public String getDetail() {
    return super.getDetail();
  }

  @Schema(
      description = "A URI reference that identifies the specific occurrence of the problem",
      example = "/api/appointments/123")
  @Override
  public URI getInstance() {
    return super.getInstance();
  }

  protected ApiProblemDetail() {
    super();
  }

  protected ApiProblemDetail(int status) {
    super(status);
  }

  public static ApiProblemDetail forStatusAndDetail(HttpStatus status, String detail) {
    ApiProblemDetail problem = new ApiProblemDetail(status.value());
    problem.setDetail(detail);
    return problem;
  }

  public static ApiProblemDetail forStatus(HttpStatus status) {
    return new ApiProblemDetail(status.value());
  }

  public ApiProblemDetail withTitle(String title) {
    this.setTitle(title);
    return this;
  }

  public ApiProblemDetail withType(URI type) {
    this.setType(type);
    return this;
  }

  public ApiProblemDetail withInstance(URI instance) {
    this.setInstance(instance);
    return this;
  }
}
