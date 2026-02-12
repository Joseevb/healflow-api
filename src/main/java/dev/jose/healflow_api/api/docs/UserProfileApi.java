package dev.jose.healflow_api.api.docs;

import dev.jose.healflow_api.api.models.UpdateUserProfileRequestDTO;
import dev.jose.healflow_api.api.models.UserProfileResponseDTO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User Profile", description = "User profile management API")
@RequestMapping("/user-profile")
public interface UserProfileApi {

  @Operation(
      operationId = "getUserProfile",
      summary = "Get user profile",
      description = "Returns the authenticated user's profile information",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "User profile",
        content = @Content(schema = @Schema(implementation = UserProfileResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping
  ResponseEntity<UserProfileResponseDTO> getUserProfile();

  @Operation(
      operationId = "updateUserProfile",
      summary = "Update user profile",
      description = "Updates the authenticated user's profile information",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Profile updated successfully",
        content = @Content(schema = @Schema(implementation = UserProfileResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ValidationProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PutMapping
  ResponseEntity<UserProfileResponseDTO> updateUserProfile(
      @RequestBody UpdateUserProfileRequestDTO body);
}
