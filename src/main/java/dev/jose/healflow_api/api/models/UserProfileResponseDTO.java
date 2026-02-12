package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "UserProfileResponse", description = "User profile information")
@Builder
public record UserProfileResponseDTO(
    @Schema(description = "User unique identifier") UUID id,
    @Schema(description = "User email") String email,
    @Schema(description = "User first name") String firstName,
    @Schema(description = "User last name") String lastName,
    @Schema(description = "User phone number") String phone,
    @Schema(description = "User date of birth") Instant dateOfBirth,
    @Schema(description = "Primary specialist") SpecialistSummaryDTO primarySpecialist,
    @Schema(description = "Whether profile is complete") Boolean isProfileComplete) {}
