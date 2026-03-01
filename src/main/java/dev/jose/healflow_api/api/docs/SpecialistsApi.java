package dev.jose.healflow_api.api.docs;

import dev.jose.healflow_api.api.models.CreateSpecialistRequestDTO;
import dev.jose.healflow_api.api.models.DayScheduleResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistAvailabilityResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistTypeResponseDTO;
import dev.jose.healflow_api.api.models.UpdateSpecialistAvailabilityRequestDTO;
import dev.jose.healflow_api.api.models.errors.ApiProblemDetail;
import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Specialists", description = "Specialist management API")
@RequestMapping("/specialists")
public interface SpecialistsApi {

  @Operation(
      operationId = "getAvailableSpecialists",
      summary = "Get available specialists",
      description = "Returns list of all active specialists",
      security = {@SecurityRequirement(name = "API Key")},
      parameters = {
        @Parameter(
            name = "type",
            description = "Specialist type",
            in = ParameterIn.QUERY,
            required = false,
            schema =
                @Schema(
                    type = "string",
                    enumAsRef = true,
                    implementation = SpecialistTypeEnum.class))
      })
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of specialists",
        content =
            @Content(
                array =
                    @ArraySchema(schema = @Schema(implementation = SpecialistResponseDTO.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping
  ResponseEntity<List<SpecialistResponseDTO>> getAvailableSpecialists(
      @RequestParam Optional<SpecialistTypeEnum> type);

  @Operation(
      operationId = "getSpecialistTypes",
      summary = "Get specialist types",
      description = "Returns list of all available specialist types")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of specialist types",
        content =
            @Content(
                array =
                    @ArraySchema(
                        schema = @Schema(implementation = SpecialistTypeResponseDTO.class)))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/types")
  ResponseEntity<List<SpecialistTypeResponseDTO>> getSpecialistTypes();

  @Operation(
      operationId = "getSpecialistBookingData",
      summary = "Get specialist booking data",
      description = "Returns available time slots for a specialist within a date range",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Specialist booking schedule",
        content =
            @Content(
                array =
                    @ArraySchema(schema = @Schema(implementation = DayScheduleResponseDTO.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Specialist not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/specialists/{specialistId}/booking-data")
  ResponseEntity<List<DayScheduleResponseDTO>> getSpecialistBookingData(
      @Parameter(description = "Specialist unique identifier") @PathVariable UUID specialistId,
      @Parameter(description = "Start date (ISO-8601 format)")
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant startDate,
      @Parameter(description = "End date (ISO-8601 format)")
          @RequestParam
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant endDate);

  @Operation(
      operationId = "createSpecialist",
      summary = "Admin: Create a new specialist",
      description = "Creates a new healthcare specialist in the system. Admin access required.",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Specialist created successfully",
        content = @Content(schema = @Schema(implementation = SpecialistResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request body",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - requires admin role",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Specialist already exists",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  ResponseEntity<SpecialistResponseDTO> createSpecialist(
      @Valid @RequestBody CreateSpecialistRequestDTO request);

  @Operation(
      operationId = "getSpecialistAvailabilities",
      summary = "Get specialist availabilities",
      description = "Returns all availability timeslots for a specialist",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of availability timeslots",
        content =
            @Content(
                array =
                    @ArraySchema(
                        schema =
                            @Schema(implementation = SpecialistAvailabilityResponseDTO.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Specialist not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/{specialistId}/availabilities")
  ResponseEntity<List<SpecialistAvailabilityResponseDTO>> getSpecialistAvailabilities(
      @Parameter(description = "Specialist unique identifier") @PathVariable UUID specialistId);

  @Operation(
      operationId = "updateSpecialistAvailability",
      summary = "Update specialist availability",
      description =
          "Updates or creates availability timeslot for a specialist. Only specialists can update"
              + " their own timeslots.",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Availability updated successfully",
        content =
            @Content(schema = @Schema(implementation = SpecialistAvailabilityResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request body",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - cannot update another specialist's timeslots",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Specialist not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PutMapping("/{specialistId}/availabilities/{dayOfWeek}")
  @PreAuthorize("hasAuthority('ROLE_SPECIALIST')")
  ResponseEntity<SpecialistAvailabilityResponseDTO> updateSpecialistAvailability(
      @Parameter(description = "Specialist unique identifier") @PathVariable UUID specialistId,
      @Parameter(description = "Day of the week") @PathVariable DayOfWeek dayOfWeek,
      @Valid @RequestBody UpdateSpecialistAvailabilityRequestDTO request);

  @Operation(
      operationId = "deleteSpecialistAvailability",
      summary = "Delete specialist availability",
      description =
          "Deletes an availability timeslot for a specialist. Only specialists can delete their own"
              + " timeslots.",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Availability deleted successfully"),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - cannot delete another specialist's timeslots",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Specialist or availability not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @DeleteMapping("/{specialistId}/availabilities/{availabilityId}")
  @PreAuthorize("hasAuthority('ROLE_SPECIALIST')")
  ResponseEntity<Void> deleteSpecialistAvailability(
      @Parameter(description = "Specialist unique identifier") @PathVariable UUID specialistId,
      @Parameter(description = "Availability record unique identifier") @PathVariable
          UUID availabilityId);
}
