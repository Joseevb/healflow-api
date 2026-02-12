package dev.jose.healflow_api.api.models;

import dev.jose.healflow_api.enumerations.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import java.time.Instant;
import lombok.Builder;

@Schema(name = "UpdateAppointmentRequest", description = "Request to update an appointment")
@Builder
public record UpdateAppointmentRequestDTO(
    @Future(message = "Appointment date must be in the future")
        @Schema(
            description = "New appointment date and time (must be in the future)",
            example = "2025-10-17T14:00:00Z",
            type = "string",
            format = "date-time")
        Instant appointmentDate,
    @Schema(description = "Appointment status", example = "CONFIRMED") AppointmentStatus status,
    @Schema(description = "Additional notes") String notes,
    @Schema(description = "Cancellation reason (required if status is CANCELLED)")
        String cancellationReason) {}
