package dev.jose.healflow_api.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.UUID;
import lombok.Builder;

@Schema(name = "TimeSlot", description = "Time slot information")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TimeSlotDTO(
    @Schema(
            description = "Time in HH:mm format",
            example = "09:00",
            requiredMode = RequiredMode.REQUIRED)
        String time,
    @Schema(
            description = "Availability status",
            allowableValues = {"available", "booked"},
            requiredMode = RequiredMode.REQUIRED)
        String status,
    @Schema(description = "Appointment ID if booked") UUID appointmentId) {}
