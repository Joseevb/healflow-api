package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Builder;

@Schema(name = "UpdateHealthMetricRequest", description = "Request to update a health metric")
@Builder
public record UpdateHealthMetricRequestDTO(
    @Schema(description = "Updated metric value", example = "125.0")
        @DecimalMin(value = "0.0", message = "Value must be positive")
        BigDecimal value,
    @Schema(description = "Updated unit of measurement", example = "mmHg") String unit,
    @Schema(
            description = "Updated recorded date",
            example = "2025-02-01T10:00:00Z",
            type = "string",
            format = "date-time")
        @PastOrPresent(message = "Recorded date cannot be in the future")
        Instant recordedAt,
    @Schema(description = "Updated notes") String notes) {}
