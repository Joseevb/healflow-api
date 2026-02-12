package dev.jose.healflow_api.api.models;

import dev.jose.healflow_api.enumerations.HealthMetricType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Schema(
    name = "HealthMetricFilterRequest",
    description = "Filter parameters for health metrics query")
@Builder
public record HealthMetricFilterRequestDTO(
    @Schema(description = "Filter by metric type", example = "BLOOD_PRESSURE_SYSTOLIC")
        HealthMetricType metricType,
    @Schema(
            description = "Start date for date range filter",
            example = "2025-01-01T00:00:00Z",
            type = "string",
            format = "date-time")
        Instant startDate,
    @Schema(
            description = "End date for date range filter",
            example = "2025-02-01T23:59:59Z",
            type = "string",
            format = "date-time")
        Instant endDate) {}
