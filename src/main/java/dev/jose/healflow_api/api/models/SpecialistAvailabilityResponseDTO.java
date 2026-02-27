package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Builder;

/**
 * Response DTO for specialist availability timeslots.
 *
 * <p>Contains information about a specialist's available working hours for a specific day.
 */
@Schema(
    name = "SpecialistAvailabilityResponse",
    description = "Specialist availability timeslot information")
@Builder
public record SpecialistAvailabilityResponseDTO(
    /** Unique identifier for this availability record. */
    @Schema(
            description = "Availability record unique identifier",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = RequiredMode.REQUIRED)
        UUID id,
    /** Specialist unique identifier. */
    @Schema(
            description = "Specialist unique identifier",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = RequiredMode.REQUIRED)
        UUID specialistId,
    /** Day of the week for this availability. */
    @Schema(
            description = "Day of the week",
            example = "MONDAY",
            requiredMode = RequiredMode.REQUIRED)
        DayOfWeek dayOfWeek,
    /** Start time of availability. */
    @Schema(
            description = "Start time of availability (HH:MM format)",
            example = "09:00",
            requiredMode = RequiredMode.REQUIRED)
        LocalTime startTime,
    /** End time of availability. */
    @Schema(
            description = "End time of availability (HH:MM format)",
            example = "17:00",
            requiredMode = RequiredMode.REQUIRED)
        LocalTime endTime,
    /** Whether this timeslot is currently available for booking. */
    @Schema(
            description = "Whether this timeslot is available for bookings",
            example = "true",
            requiredMode = RequiredMode.REQUIRED)
        Boolean isAvailable) {}
