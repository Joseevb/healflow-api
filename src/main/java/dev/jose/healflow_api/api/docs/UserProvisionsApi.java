package dev.jose.healflow_api.api.docs;

import dev.jose.healflow_api.api.models.ProvisionUserRequestDTO;
import dev.jose.healflow_api.api.models.ValidateAuthUserIdsDTO;
import dev.jose.healflow_api.api.models.errors.ApiProblemDetail;
import dev.jose.healflow_api.api.models.errors.ValidationProblemDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(
    name = "User Provisions",
    description = "Provision a user before it's created on the authentication service")
@RequestMapping("/user-provisions")
public interface UserProvisionsApi {

  @Operation(
      operationId = "provisionUser",
      summary = "Provision a user",
      description =
          """
          Creates a new Domain User. This action is triggered by the authentication service when a user is created.
          """,
      security = {@SecurityRequirement(name = "API Key Auth")})
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "User provisioned successfully"),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ValidationProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
  })
  @PostMapping
  ResponseEntity<Void> provisionUser(
      @RequestBody ProvisionUserRequestDTO body, UriComponentsBuilder uriBuilder);

  @Operation(
      operationId = "validateUser",
      summary = "Validate user IDs",
      description =
          """
          Validates that the provided user IDs exist in the system. This is used by the authentication
          service to ensure user IDs are in sync between systems.
          """,
      security = {@SecurityRequirement(name = "API Key Auth")})
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "All user IDs are valid"),
    @ApiResponse(
        responseCode = "400",
        description = "One or more user IDs are invalid",
        content = @Content(schema = @Schema(implementation = ValidationProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
  })
  @PostMapping("/validate")
  ResponseEntity<Void> validateUser(@RequestBody ValidateAuthUserIdsDTO body);
}
