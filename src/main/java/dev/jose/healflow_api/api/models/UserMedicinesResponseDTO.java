package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "UserMedicinesResponse", description = "User medicines response")
@Builder
public record UserMedicinesResponseDTO(
    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000") UUID userId,
    @Schema(description = "Medicine ID", example = "123") Integer medicineId,
    @Schema(description = "Medicine name", example = "Aspirin") String medicineName,
    @Schema(description = "Medicine dosage", example = "500mg") String dosage,
    @Schema(description = "Medicine frequency", example = "Twice daily") String frequency,
    @Schema(description = "Start date of medication", example = "2026-02-01T09:00:00")
        LocalDateTime startDate,
    @Schema(description = "End date of medication", example = "2026-02-15T09:00:00")
        LocalDateTime endDate) {}
