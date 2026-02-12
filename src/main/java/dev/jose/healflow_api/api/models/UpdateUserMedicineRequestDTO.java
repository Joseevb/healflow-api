package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Schema(name = "UpdateUserMedicineRequest", description = "Update user medicine request")
@Builder
public record UpdateUserMedicineRequestDTO(
    @Schema(description = "Medicine dosage", example = "500mg")
        @NotBlank(message = "Dosage is required")
        String dosage,
    @Schema(description = "Medicine frequency", example = "Twice daily")
        @NotBlank(message = "Frequency is required")
        String frequency,
    @Schema(description = "Start date of medication", example = "2026-02-01T09:00:00")
        @NotNull(message = "Start date is required")
        LocalDateTime startDate,
    @Schema(description = "End date of medication", example = "2026-02-15T09:00:00")
        @NotNull(message = "End date is required")
        LocalDateTime endDate) {}
