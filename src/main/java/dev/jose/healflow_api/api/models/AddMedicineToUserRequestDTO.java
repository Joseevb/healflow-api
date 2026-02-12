package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "AddMedicineToUserRequest", description = "Add medicine to user request")
@Builder
public record AddMedicineToUserRequestDTO(
    UUID userId,
    Integer medicineId,
    String dosage,
    String frequency,
    LocalDateTime startDate,
    LocalDateTime endDate) {}
