package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Builder;

/**
 * Request DTO for updating specialist availability timeslots.
 *
 * <p>Specialists use this DTO to define their working hours for each day of the week.
 */
@Schema(
    name = "UpdateSpecialistAvailabilityRequest",
    description = "Request to update specialist availability timeslots")
@Builder
public record UpdateSpecialistAvailabilityRequestDTO(
    /**
     * Day of the week for the availability.
     *
     * <p>Required field specifying which day this timeslot applies to.
     */
    @Schema(
            description = "Day of the week",
            example = "MONDAY",
            requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "Day of week is required")
        DayOfWeek dayOfWeek,
    /**
     * Start time of availability.
     *
     * <p>Required field in HH:MM format (24-hour).
     */
    @Schema(
            description = "Start time of availability (HH:MM format)",
            example = "09:00",
            requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "Start time is required")
        LocalTime startTime,
    /**
     * End time of availability.
     *
     * <p>Required field in HH:MM format (24-hour). Must be after start time.
     */
    @Schema(
            description = "End time of availability (HH:MM format)",
            example = "17:00",
            requiredMode = RequiredMode.REQUIRED)
        @NotNull(message = "End time is required")
        LocalTime endTime,
    /**
     * Whether this timeslot is currently available for booking.
     *
     * <p>Optional field, defaults to true if not specified.
     */
    @Schema(
            description = "Whether this timeslot is available for bookings",
            example = "true",
            requiredMode = RequiredMode.NOT_REQUIRED)
        Boolean isAvailable) {}
