package dev.jose.healflow_api.api.docs;

import dev.jose.healflow_api.api.models.AppointmentResponseDTO;
import dev.jose.healflow_api.api.models.CreateAppointmentRequestDTO;
import dev.jose.healflow_api.api.models.UpdateAppointmentRequestDTO;
import dev.jose.healflow_api.api.models.errors.ApiProblemDetail;
import dev.jose.healflow_api.api.models.errors.ValidationProblemDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Appointments", description = "Appointment management API")
@RequestMapping("/appointments")
public interface AppointmentsApi {

  @Operation(
      operationId = "getUserAppointments",
      summary = "Get user appointments",
      description = "Returns all appointments for the authenticated user",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of user appointments",
        content =
            @Content(
                array =
                    @ArraySchema(schema = @Schema(implementation = AppointmentResponseDTO.class)))),
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
  ResponseEntity<List<AppointmentResponseDTO>> getUserAppointments(
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "getUpcomingAppointments",
      summary = "Get upcoming appointments",
      description = "Returns upcoming appointments for the authenticated user",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of upcoming appointments",
        content =
            @Content(
                array =
                    @ArraySchema(schema = @Schema(implementation = AppointmentResponseDTO.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/upcoming")
  ResponseEntity<List<AppointmentResponseDTO>> getUpcomingAppointments(
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "getPastAppointments",
      summary = "Get past appointments",
      description = "Returns past appointments for the authenticated user",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of past appointments",
        content =
            @Content(
                array =
                    @ArraySchema(schema = @Schema(implementation = AppointmentResponseDTO.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/history")
  ResponseEntity<List<AppointmentResponseDTO>> getPastAppointments(
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "getAppointmentById",
      summary = "Get appointment by ID",
      description = "Returns a specific appointment by its unique identifier",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Appointment details",
        content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - not your appointment",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Appointment not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/{id}")
  ResponseEntity<AppointmentResponseDTO> getAppointmentById(
      @Parameter(description = "Appointment unique identifier") @PathVariable UUID id,
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "createAppointment",
      summary = "Create appointment",
      description = "Creates a new appointment with a specialist",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Appointment created successfully",
        content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ValidationProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Specialist not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "409",
        description = "Time slot already booked",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PostMapping
  ResponseEntity<AppointmentResponseDTO> createAppointment(
      @Parameter(hidden = true) @RequestAttribute UUID userId,
      @Valid @RequestBody CreateAppointmentRequestDTO request,
      UriComponentsBuilder uriBuilder);

  @Operation(
      operationId = "updateAppointment",
      summary = "Update appointment",
      description = "Updates an existing appointment",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Appointment updated successfully",
        content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ValidationProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - not your appointment",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Appointment not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "409",
        description = "New time slot already booked",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PutMapping("/{id}")
  ResponseEntity<AppointmentResponseDTO> updateAppointment(
      @Parameter(description = "Appointment unique identifier") @PathVariable UUID id,
      @Parameter(hidden = true) @RequestAttribute UUID userId,
      @Valid @RequestBody UpdateAppointmentRequestDTO request);

  @Operation(
      operationId = "cancelAppointment",
      summary = "Cancel appointment",
      description = "Cancels an existing appointment",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Appointment cancelled successfully"),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - not your appointment",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Appointment not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @DeleteMapping("/{id}")
  ResponseEntity<Void> cancelAppointment(
      @Parameter(description = "Appointment unique identifier") @PathVariable UUID id,
      @Parameter(hidden = true) @RequestAttribute UUID userId,
      @Parameter(description = "Cancellation reason") @RequestParam String reason);
}
