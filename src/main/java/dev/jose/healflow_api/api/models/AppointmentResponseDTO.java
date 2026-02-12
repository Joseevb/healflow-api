package dev.jose.healflow_api.api.models;

import dev.jose.healflow_api.enumerations.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "AppointmentResponse", description = "Appointment details")
@Builder
public record AppointmentResponseDTO(
    @Schema(description = "Appointment unique identifier", requiredMode = RequiredMode.REQUIRED)
        UUID id,
    @Schema(description = "Client information", requiredMode = RequiredMode.REQUIRED)
        ClientSummaryDTO client,
    @Schema(description = "Specialist information", requiredMode = RequiredMode.REQUIRED)
        SpecialistSummaryDTO specialist,
    @Schema(
            description = "Appointment date and time",
            example = "2025-10-17T10:00:00Z",
            type = "string",
            format = "date-time",
            requiredMode = RequiredMode.REQUIRED)
        Instant appointmentDate,
    @Schema(
            description = "Duration in minutes",
            example = "30",
            requiredMode = RequiredMode.REQUIRED)
        Short durationMinutes,
    @Schema(
            description = "Appointment status",
            example = "CONFIRMED",
            requiredMode = RequiredMode.REQUIRED)
        AppointmentStatus status,
    @Schema(description = "Additional notes") String notes,
    @Schema(description = "Cancellation reason if cancelled") String cancellationReason,
    @Schema(description = "Creation timestamp", requiredMode = RequiredMode.REQUIRED)
        Instant createdAt,
    @Schema(description = "Last update timestamp", requiredMode = RequiredMode.REQUIRED)
        Instant updatedAt) {}
