package dev.jose.healflow_api.api.models;

import dev.jose.healflow_api.enumerations.HealthMetricType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "HealthMetricResponse", description = "Health metric details")
@Builder
public record HealthMetricResponseDTO(
    @Schema(description = "Health metric unique identifier", requiredMode = RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Type of health metric", requiredMode = RequiredMode.REQUIRED)
        HealthMetricType metricType,
    @Schema(description = "Metric value", example = "120.5", requiredMode = RequiredMode.REQUIRED)
        BigDecimal value,
    @Schema(description = "Unit of measurement", example = "mmHg", requiredMode = RequiredMode.REQUIRED)
        String unit,
    @Schema(
            description = "When the metric was recorded",
            example = "2025-02-01T10:00:00Z",
            type = "string",
            format = "date-time",
            requiredMode = RequiredMode.REQUIRED)
        Instant recordedAt,
    @Schema(description = "Additional notes about the measurement") String notes,
    @Schema(description = "Source of the measurement", example = "appointment")
        String source,
    @Schema(description = "ID of specialist who recorded this metric")
        UUID recordedBySpecialistId,
    @Schema(
            description = "Creation timestamp",
            type = "string",
            format = "date-time",
            requiredMode = RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(
            description = "Last update timestamp",
            type = "string",
            format = "date-time",
            requiredMode = RequiredMode.REQUIRED)
        Instant updatedAt) {}
