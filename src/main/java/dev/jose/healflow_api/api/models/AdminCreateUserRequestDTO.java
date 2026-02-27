package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

/**
 * Request DTO for admin creation/update of users.
 *
 * <p>Admins use this DTO to create or update user profiles with a provided auth ID from an external
 * authentication system.
 */
@Schema(name = "AdminCreateUserRequest", description = "Admin request to create or update a user")
@Builder
public record AdminCreateUserRequestDTO(
    /**
     * Authentication user ID from external auth system.
     *
     * <p>Required field. This UUID links the user record to the authentication system.
     */
    @Schema(
            description = "Authentication user ID from external auth system",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "Auth ID is required")
        UUID authId,
    /**
     * User's email address.
     *
     * <p>Required field. Must be a valid email format and unique across the system.
     */
    @Schema(
            description = "User email address",
            example = "john.doe@example.com",
            requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
    /**
     * User's first name.
     *
     * <p>Required field.
     */
    @Schema(description = "User first name", example = "John", requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "First name is required")
        String firstName,
    /**
     * User's last name.
     *
     * <p>Required field.
     */
    @Schema(description = "User last name", example = "Doe", requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "Last name is required")
        String lastName,
    /**
     * User's phone number.
     *
     * <p>Optional field for contact purposes.
     */
    @Schema(
            description = "User phone number",
            example = "+1-555-0123",
            requiredMode = RequiredMode.NOT_REQUIRED)
        String phone,
    /**
     * User's date of birth.
     *
     * <p>Optional field in ISO-8601 format.
     */
    @Schema(
            description = "User date of birth (ISO-8601 format)",
            example = "1990-01-15T00:00:00Z",
            requiredMode = RequiredMode.NOT_REQUIRED)
        Instant dateOfBirth,
    /**
     * Primary specialist ID for this user.
     *
     * <p>Required field. Associates the user with their primary healthcare specialist.
     */
    @Schema(
            description = "Primary specialist unique identifier",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "Primary specialist ID is required")
        UUID primarySpecialistId,
    /**
     * User subscription status.
     *
     * <p>Optional field. Defaults to false if not specified.
     */
    @Schema(
            description = "Whether user is subscribed",
            example = "false",
            requiredMode = RequiredMode.NOT_REQUIRED)
        Boolean isSubscribed,
    /**
     * User active status.
     *
     * <p>Optional field. Defaults to true if not specified.
     */
    @Schema(
            description = "Whether user account is active",
            example = "true",
            requiredMode = RequiredMode.NOT_REQUIRED)
        Boolean isActive) {}
