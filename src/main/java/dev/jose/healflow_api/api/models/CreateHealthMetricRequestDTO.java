package dev.jose.healflow_api.api.models;

import dev.jose.healflow_api.enumerations.HealthMetricType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Schema(name = "CreateHealthMetricRequest", description = "Request to create a health metric")
@Builder
public record CreateHealthMetricRequestDTO(
    @Schema(
            description = "Type of health metric",
            example = "BLOOD_PRESSURE_SYSTOLIC",
            requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "Metric type is required")
        HealthMetricType metricType,
    @Schema(description = "Metric value", example = "120.5", requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "Value is required")
        @DecimalMin(value = "0.0", message = "Value must be positive")
        BigDecimal value,
    @Schema(description = "Unit of measurement", example = "mmHg", requiredMode = RequiredMode.REQUIRED)
        @NotBlank(message = "Unit is required")
        String unit,
    @Schema(
            description = "When the metric was recorded",
            example = "2025-02-01T10:00:00Z",
            type = "string",
            format = "date-time",
            requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "Recorded date is required")
        @PastOrPresent(message = "Recorded date cannot be in the future")
        Instant recordedAt,
    @Schema(description = "Additional notes about the measurement") String notes,
    @Schema(description = "Source of the measurement", example = "appointment")
        String source) {}
