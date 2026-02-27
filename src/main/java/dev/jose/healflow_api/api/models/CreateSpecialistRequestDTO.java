package dev.jose.healflow_api.api.models;

import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

/**
 * Request DTO for creating a new specialist.
 *
 * <p>Contains all required information to register a healthcare specialist in the system.
 */
@Schema(name = "CreateSpecialistRequest", description = "Request to create a new specialist")
@Builder
public record CreateSpecialistRequestDTO(
    /**
     * Authenticated user ID who is creating this specialist.
     *
     * <p>This should be the UUID of the admin or authorized user performing the specialist
     * registration.
     */
    @Schema(
            description = "Authenticated user ID who is creating this specialist",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "Auth user ID is required")
        UUID authUserId,
    /**
     * Specialist's first name.
     *
     * <p>Required field, must be between 1 and 100 characters.
     */
    @Schema(
            description = "Specialist first name",
            example = "Sarah",
            requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "First name is required")
        String firstName,
    /**
     * Specialist's last name.
     *
     * <p>Required field, must be between 1 and 100 characters.
     */
    @Schema(
            description = "Specialist last name",
            example = "Johnson",
            requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "Last name is required")
        String lastName,
    /**
     * Specialist's email address.
     *
     * <p>Must be a valid email format and unique across the system.
     */
    @Schema(
            description = "Specialist email address",
            example = "sarah.johnson@hospital.com",
            requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
    /**
     * Specialist's medical license number.
     *
     * <p>Must be unique across the system. This is typically used for verification purposes.
     */
    @Schema(
            description = "Medical license number",
            example = "LIC123456789",
            requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "License number is required")
        String licenseNumber,
    /**
     * Specialist's medical specialty type.
     *
     * <p>Determines the category of healthcare services the specialist provides.
     */
    @Schema(
            description = "Specialist medical specialty",
            example = "CARDIOLOGIST",
            requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "Specialty is required")
        SpecialistTypeEnum specialty,
    /**
     * Specialist's phone number.
     *
     * <p>Optional field for contact purposes.
     */
    @Schema(
            description = "Specialist phone number",
            example = "+1-555-0123",
            requiredMode = RequiredMode.NOT_REQUIRED)
        String phone,
    /**
     * Default consultation duration in minutes.
     *
     * <p>Defaults to 30 minutes if not specified.
     */
    @Schema(
            description = "Default consultation duration in minutes",
            example = "30",
            requiredMode = RequiredMode.NOT_REQUIRED)
        Short consultationDurationMinutes,
    @Schema(
            description = "Profile picture name",
            example = "profile.jpg",
            requiredMode = RequiredMode.NOT_REQUIRED)
        String profilePictureName) {}
