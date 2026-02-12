package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "ClientSummary", description = "Summary of client information")
@Builder
public record ClientSummaryDTO(
    @Schema(description = "Client unique identifier", requiredMode = RequiredMode.REQUIRED) UUID id,
    @Schema(
            description = "Client full name",
            example = "John Doe",
            requiredMode = RequiredMode.REQUIRED)
        String name,
    @Schema(
            description = "Client email",
            example = "john.doe@email.com",
            requiredMode = RequiredMode.REQUIRED)
        String email) {}
