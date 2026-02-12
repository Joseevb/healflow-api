package dev.jose.healflow_api.api.models;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Schema(name = "DayScheduleResponse", description = "Schedule for a specific day")
@Builder
public record DayScheduleResponseDTO(
    @Schema(
            description = "Date for this schedule",
            example = "2025-10-17T00:00:00Z",
            type = "string",
            format = "date-time",
            requiredMode = RequiredMode.REQUIRED)
        Instant date,
    @Schema(description = "List of time slots for this day", requiredMode = RequiredMode.REQUIRED)
        List<TimeSlotDTO> timeslots) {}
