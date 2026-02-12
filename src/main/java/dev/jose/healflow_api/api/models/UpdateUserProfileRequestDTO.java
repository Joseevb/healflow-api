package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.Instant;
import lombok.Builder;

@Schema(name = "UpdateUserProfileRequest", description = "Request to update user profile")
@Builder
public record UpdateUserProfileRequestDTO(
    @Schema(description = "User first name", requiredMode = RequiredMode.NOT_REQUIRED)
        String firstName,
    @Schema(description = "User last name", requiredMode = RequiredMode.NOT_REQUIRED) String lastName,
    @Schema(description = "User phone number", requiredMode = RequiredMode.NOT_REQUIRED) String phone,
    @Schema(description = "User date of birth", requiredMode = RequiredMode.NOT_REQUIRED)
        Instant dateOfBirth) {}
