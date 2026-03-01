package dev.jose.healflow_api.api.docs;

import dev.jose.healflow_api.api.models.AdminCreateUserRequestDTO;
import dev.jose.healflow_api.api.models.AdminUserProfileResponseDTO;
import dev.jose.healflow_api.api.models.UpdateUserProfileRequestDTO;
import dev.jose.healflow_api.api.models.UserProfileResponseDTO;
import dev.jose.healflow_api.api.models.errors.ApiProblemDetail;
import dev.jose.healflow_api.api.models.errors.ValidationProblemDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

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

  @Operation(
      operationId = "adminCreateOrUpdateUser",
      summary = "Admin: Create or update user",
      description =
          "Admin endpoint to create a new user or update an existing user with provided auth ID",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "User created or updated successfully",
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
        responseCode = "403",
        description = "Forbidden - requires admin role",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Specialist not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Email or auth ID already exists",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PostMapping("/admin/users")
  @ResponseStatus(HttpStatus.CREATED)
  ResponseEntity<UserProfileResponseDTO> adminCreateOrUpdateUser(
      @Valid @RequestBody AdminCreateUserRequestDTO request);

  @Operation(
      operationId = "getAllUsers",
      summary = "Admin: Get all users",
      description = "Returns a paginated list of all users in the system. Admin access required.",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Paginated list of users",
        content = @Content(schema = @Schema(implementation = AdminUserProfileResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - requires admin role",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping("/admin/users")
  ResponseEntity<AdminUserProfileResponseDTO> getAllUsers(
      @Parameter(description = "Zero-indexed page number", example = "0", required = false)
          @RequestParam(defaultValue = "0")
          Integer page,
      @Parameter(
              description = "Page size (number of users per page)",
              example = "20",
              required = false)
          @RequestParam(required = false)
          Integer pageSize);
}
