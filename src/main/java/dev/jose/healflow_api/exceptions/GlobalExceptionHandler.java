package dev.jose.healflow_api.exceptions;

import dev.jose.healflow_api.api.models.errors.ApiProblemDetail;
import dev.jose.healflow_api.api.models.errors.ValidationProblemDetail;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final String ERROR_TYPE_BASE = "https://api.healflow.dev/errors";

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiProblemDetail> handleNotFoundException(
      NotFoundException ex, HttpServletRequest request) {
    log.warn("Resource not found: {}", ex.getMessage());

    ApiProblemDetail problem =
        ApiProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage())
            .withTitle("Resource Not Found")
            .withType(URI.create(ERROR_TYPE_BASE + "/not-found"))
            .withInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiProblemDetail> handleConflictException(
      ConflictException ex, HttpServletRequest request) {
    log.warn("Conflict: {}", ex.getMessage());

    ApiProblemDetail problem =
        ApiProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage())
            .withTitle("Conflict")
            .withType(URI.create(ERROR_TYPE_BASE + "/conflict"))
            .withInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ApiProblemDetail> handleForbiddenException(
      ForbiddenException ex, HttpServletRequest request) {
    log.warn("Forbidden: {}", ex.getMessage());

    ApiProblemDetail problem =
        ApiProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage())
            .withTitle("Forbidden")
            .withType(URI.create(ERROR_TYPE_BASE + "/forbidden"))
            .withInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiProblemDetail> handleValidationException(
      ValidationException ex, HttpServletRequest request) {
    log.warn("Validation error: {}", ex.getMessage());

    ApiProblemDetail problem =
        ApiProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage())
            .withTitle("Validation Error")
            .withType(URI.create(ERROR_TYPE_BASE + "/validation"))
            .withInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(AuthUserIdValidationException.class)
  public ResponseEntity<ValidationProblemDetail> handleAuthUserIdValidationException(
      AuthUserIdValidationException ex, HttpServletRequest request) {
    log.error("Auth user ID validation error: {}", ex.getMessage());

    ValidationProblemDetail problem =
        ValidationProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage())
            .withTitle("Invalid User IDs")
            .withType(URI.create(ERROR_TYPE_BASE + "/invalid-user-ids"))
            .withInstance(URI.create(request.getRequestURI()));
    problem.setInvalidIds(ex.getInvalidIds());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiProblemDetail> handleGenericException(
      Exception ex, HttpServletRequest request) {
    log.error("Unexpected error", ex);

    ApiProblemDetail problem =
        ApiProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")
            .withTitle("Internal Server Error")
            .withType(URI.create(ERROR_TYPE_BASE + "/internal-error"))
            .withInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.warn("Method argument not valid: {}", ex.getMessage());

    String requestUri = "";
    if (request instanceof ServletWebRequest servletWebRequest) {
      requestUri = servletWebRequest.getRequest().getRequestURI();
    }

    ApiProblemDetail problem =
        ApiProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Request validation failed. Check your input.")
            .withTitle("Validation Error")
            .withType(URI.create(ERROR_TYPE_BASE + "/validation"))
            .withInstance(URI.create(requestUri));

    // Add field errors as properties
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            fieldError ->
                problem.setProperty(
                    fieldError.getField(),
                    fieldError.getDefaultMessage() != null
                        ? fieldError.getDefaultMessage()
                        : "Invalid value"));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }
}
