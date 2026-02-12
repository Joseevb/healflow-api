package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "UserMedicineCountResponse", description = "User medicine count response")
@Builder
public record UserMedicineCountResponseDTO(
    @Schema(description = "Total count of medicines for the user", example = "5") Long count) {}
