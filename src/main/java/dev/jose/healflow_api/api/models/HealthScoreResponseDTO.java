package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "HealthScoreResponse", description = "User health score with detailed breakdown")
@Builder
public record HealthScoreResponseDTO(
    @Schema(description = "Health score unique identifier", requiredMode = RequiredMode.REQUIRED)
        UUID id,
    @Schema(
            description = "Overall health score (0-100)",
            example = "78",
            requiredMode = RequiredMode.REQUIRED)
        Integer overallScore,
    @Schema(description = "Cardiovascular health subscore (0-100)", example = "82")
        Integer cardiovascularScore,
    @Schema(description = "Metabolic health subscore (0-100)", example = "75")
        Integer metabolicScore,
    @Schema(description = "Lifestyle habits subscore (0-100)", example = "70")
        Integer lifestyleScore,
    @Schema(description = "Vital signs subscore (0-100)", example = "85")
        Integer vitalSignsScore,
    @Schema(
            description = "When the score was calculated",
            example = "2025-02-01T10:00:00Z",
            type = "string",
            format = "date-time",
            requiredMode = RequiredMode.REQUIRED)
        Instant calculatedAt,
    @Schema(
            description = "Number of data points used in calculation",
            example = "25",
            requiredMode = RequiredMode.REQUIRED)
        Integer dataPointsCount,
    @Schema(
            description = "Time period in days for data analysis",
            example = "90",
            requiredMode = RequiredMode.REQUIRED)
        Integer periodDays,
    @Schema(description = "Summary of recent health metrics")
        List<HealthMetricSummaryDTO> recentMetrics,
    @Schema(description = "Health recommendations based on the score")
        List<RecommendationDTO> recommendations) {}
