package dev.jose.healflow_api.api.models;

import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "SpecialistSummary", description = "Summary of specialist information")
@Builder
public record SpecialistSummaryDTO(
    @Schema(description = "Specialist unique identifier", requiredMode = RequiredMode.REQUIRED)
        UUID id,
    @Schema(
            description = "Specialist full name",
            example = "Dr. Sarah Johnson",
            requiredMode = RequiredMode.REQUIRED)
        String name,
    @Schema(description = "Specialty", example = "Cardiology", requiredMode = RequiredMode.REQUIRED)
        SpecialistTypeEnum specialty,
    @Schema(description = "Profile picture name", requiredMode = RequiredMode.REQUIRED)
        String profilePictureName) {}
