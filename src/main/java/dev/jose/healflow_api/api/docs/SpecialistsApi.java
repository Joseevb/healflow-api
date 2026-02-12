package dev.jose.healflow_api.api.docs;

import dev.jose.healflow_api.api.models.DayScheduleResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistTypeResponseDTO;
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
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
