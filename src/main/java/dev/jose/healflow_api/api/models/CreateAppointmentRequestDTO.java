package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "CreateAppointmentRequest", description = "Request to create a new appointment")
@Builder
public record CreateAppointmentRequestDTO(
    @NotNull(message = "Specialist ID is required")
        @Schema(
            description = "Specialist unique identifier",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED)
        UUID specialistId,
    @NotNull(message = "Appointment date is required")
        @Future(message = "Appointment date must be in the future")
        @Schema(
            description = "Appointment date and time (must be in the future)",
            example = "2025-10-17T10:00:00Z",
            type = "string",
            format = "date-time",
            requiredMode = Schema.RequiredMode.REQUIRED)
        Instant appointmentDate,
    @Schema(description = "Additional notes for the appointment") String notes) {}
