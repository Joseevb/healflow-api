package dev.jose.healflow_api.api.models;

import dev.jose.healflow_api.annotations.NullableNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "ProvisionUserRequest", description = "Request to provision a user")
@Builder
public record ProvisionUserRequestDTO(
    @Schema(description = "User ID", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "User ID is required")
        UUID userId,
    @Schema(description = "User email", requiredMode = RequiredMode.REQUIRED)
        @Email(message = "User email is not valid")
        @NotBlank(message = "User email is required")
        String email,
    @Schema(description = "Primary specialist ID", requiredMode = RequiredMode.NOT_REQUIRED)
        @Deprecated
        UUID specialistId,
    @Schema(description = "User first name", requiredMode = RequiredMode.NOT_REQUIRED)
        @NullableNotBlank(message = "User first name is required")
        String firstName,
    @Schema(description = "User last name", requiredMode = RequiredMode.NOT_REQUIRED)
        @NullableNotBlank(message = "User last name is required")
        String lastName,
    @Schema(description = "User phone number", requiredMode = RequiredMode.NOT_REQUIRED)
        @NotBlank(message = "User phone number is required")
        String phone,
    @Schema(description = "User subscription status", requiredMode = RequiredMode.NOT_REQUIRED)
        @NotNull(message = "User subscription status is required")
        Boolean isSubscribed) {}
