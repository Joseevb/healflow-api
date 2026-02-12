package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "ProvisionUserRequest", description = "Request to provision a user")
@Builder
public record ProvisionUserRequestDTO(
    @Schema(description = "User ID", requiredMode = RequiredMode.REQUIRED) UUID userId,
    @Schema(description = "User email", requiredMode = RequiredMode.REQUIRED) String email,
    @Schema(description = "Primary specialist ID", requiredMode = RequiredMode.NOT_REQUIRED)
        UUID specialistId,
    @Schema(description = "User first name", requiredMode = RequiredMode.NOT_REQUIRED)
        String firstName,
    @Schema(description = "User last name", requiredMode = RequiredMode.NOT_REQUIRED)
        String lastName,
    @Schema(description = "User phone number", requiredMode = RequiredMode.NOT_REQUIRED)
        String phone,
    @Schema(description = "User subscription status", requiredMode = RequiredMode.NOT_REQUIRED)
        Boolean isSubscribed) {}
