package dev.jose.healflow_api.api.models;

import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "SpecialistResponse", description = "Specialist information")
@Builder
public record SpecialistResponseDTO(
    @Schema(
            description = "Specialist unique identifier",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = RequiredMode.REQUIRED)
        UUID id,
    @Schema(
            description = "Specialist full name",
            example = "Dr. Sarah Johnson",
            requiredMode = RequiredMode.REQUIRED)
        String name,
    @Schema(description = "Specialist type", requiredMode = RequiredMode.REQUIRED)
        SpecialistTypeEnum specialty,
    @Schema(
            description = "Email address",
            example = "sarah.johnson@hospital.com",
            requiredMode = RequiredMode.REQUIRED)
        String email,
    @Schema(description = "Profile picture name", requiredMode = RequiredMode.REQUIRED)
        String profilePictureName,
    @Schema(description = "Phone number", example = "+1-555-0123") String phone,
    @Schema(
            description = "Whether the specialist is accepting new patients",
            requiredMode = RequiredMode.REQUIRED)
        Boolean isActive,
    @Schema(
            description = "Default consultation duration in minutes",
            example = "30",
            requiredMode = RequiredMode.REQUIRED)
        Short consultationDurationMinutes) {}
