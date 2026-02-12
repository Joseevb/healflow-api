package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;

@Schema(
    name = "BatchCreateHealthMetricsRequest",
    description = "Request to create multiple health metrics at once")
@Builder
public record BatchCreateHealthMetricsRequestDTO(
    @Schema(
            description = "List of health metrics to create",
            requiredMode = RequiredMode.REQUIRED)
        @NotEmpty(message = "Metrics list cannot be empty")
        @Valid
        List<CreateHealthMetricRequestDTO> metrics) {}
