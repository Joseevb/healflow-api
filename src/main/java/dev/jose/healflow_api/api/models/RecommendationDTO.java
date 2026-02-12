package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.Builder;

@Schema(name = "Recommendation", description = "Health recommendation based on metrics")
@Builder
public record RecommendationDTO(
    @Schema(
            description = "Recommendation category",
            example = "cardiovascular",
            requiredMode = RequiredMode.REQUIRED)
        String category,
    @Schema(
            description = "Recommendation message",
            example = "Your blood pressure is slightly elevated. Consider reducing sodium intake.",
            requiredMode = RequiredMode.REQUIRED)
        String message,
    @Schema(
            description = "Priority level",
            example = "medium",
            allowableValues = {"low", "medium", "high"},
            requiredMode = RequiredMode.REQUIRED)
        String priority) {}
