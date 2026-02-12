package dev.jose.healflow_api.api.models;

import dev.jose.healflow_api.enumerations.HealthMetricType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Schema(name = "HealthMetricSummary", description = "Summary of a health metric type")
@Builder
public record HealthMetricSummaryDTO(
    @Schema(description = "Type of health metric", requiredMode = RequiredMode.REQUIRED)
        HealthMetricType metricType,
    @Schema(description = "Latest recorded value", example = "120.5", requiredMode = RequiredMode.REQUIRED)
        BigDecimal latestValue,
    @Schema(description = "Unit of measurement", example = "mmHg", requiredMode = RequiredMode.REQUIRED)
        String unit,
    @Schema(
            description = "When the metric was last recorded",
            example = "2025-02-01T10:00:00Z",
            type = "string",
            format = "date-time",
            requiredMode = RequiredMode.REQUIRED)
        Instant recordedAt,
    @Schema(
            description = "Trend analysis",
            example = "improving",
            allowableValues = {"improving", "stable", "declining", "insufficient_data"})
        String trend) {}
