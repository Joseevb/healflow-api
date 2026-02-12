package dev.jose.healflow_api.api.docs;

import dev.jose.healflow_api.api.models.*;
import dev.jose.healflow_api.api.models.errors.ApiProblemDetail;
import dev.jose.healflow_api.api.models.errors.ValidationProblemDetail;
import dev.jose.healflow_api.enumerations.HealthMetricType;
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
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Health Metrics", description = "Health metrics and score management API")
@RequestMapping("/health-metrics")
public interface HealthMetricsApi {

  @Operation(
      operationId = "getUserHealthMetrics",
      summary = "Get user health metrics",
      description = "Returns all health metrics for the authenticated user",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of user health metrics",
        content =
            @Content(
                array =
                    @ArraySchema(
                        schema = @Schema(implementation = HealthMetricResponseDTO.class)))),
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
  ResponseEntity<List<HealthMetricResponseDTO>> getUserHealthMetrics(
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "getFilteredHealthMetrics",
      summary = "Get filtered health metrics",
      description = "Returns filtered health metrics for the authenticated user",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of filtered health metrics",
        content =
            @Content(
                array =
                    @ArraySchema(
                        schema = @Schema(implementation = HealthMetricResponseDTO.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/filter")
  ResponseEntity<List<HealthMetricResponseDTO>> getFilteredHealthMetrics(
      @Parameter(hidden = true) @RequestAttribute UUID userId,
      @Parameter(description = "Filter by metric type") @RequestParam(required = false)
          HealthMetricType metricType,
      @Parameter(description = "Start date for date range filter") @RequestParam(required = false)
          Instant startDate,
      @Parameter(description = "End date for date range filter") @RequestParam(required = false)
          Instant endDate);

  @Operation(
      operationId = "getHealthMetricById",
      summary = "Get health metric by ID",
      description = "Returns a specific health metric by its unique identifier",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Health metric details",
        content = @Content(schema = @Schema(implementation = HealthMetricResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - not your metric",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Health metric not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/{id}")
  ResponseEntity<HealthMetricResponseDTO> getHealthMetricById(
      @Parameter(description = "Health metric unique identifier") @PathVariable UUID id,
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "getLatestMetricByType",
      summary = "Get latest metric by type",
      description = "Returns the most recent health metric of a specific type",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Latest health metric of specified type",
        content = @Content(schema = @Schema(implementation = HealthMetricResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No metric found of this type",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/latest/{metricType}")
  ResponseEntity<HealthMetricResponseDTO> getLatestMetricByType(
      @Parameter(description = "Type of health metric") @PathVariable HealthMetricType metricType,
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "createHealthMetric",
      summary = "Create health metric",
      description = "Creates a new health metric entry",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Health metric created successfully",
        content = @Content(schema = @Schema(implementation = HealthMetricResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ValidationProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PostMapping
  ResponseEntity<HealthMetricResponseDTO> createHealthMetric(
      @Parameter(hidden = true) @RequestAttribute UUID userId,
      @Valid @RequestBody CreateHealthMetricRequestDTO request,
      UriComponentsBuilder uriBuilder);

  @Operation(
      operationId = "createHealthMetricsBatch",
      summary = "Create multiple health metrics",
      description = "Creates multiple health metric entries at once",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Health metrics created successfully",
        content =
            @Content(
                array =
                    @ArraySchema(
                        schema = @Schema(implementation = HealthMetricResponseDTO.class)))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ValidationProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PostMapping("/batch")
  ResponseEntity<List<HealthMetricResponseDTO>> createHealthMetricsBatch(
      @Parameter(hidden = true) @RequestAttribute UUID userId,
      @Valid @RequestBody BatchCreateHealthMetricsRequestDTO request);

  @Operation(
      operationId = "updateHealthMetric",
      summary = "Update health metric",
      description = "Updates an existing health metric",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Health metric updated successfully",
        content = @Content(schema = @Schema(implementation = HealthMetricResponseDTO.class))),
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
        description = "Forbidden - not your metric",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Health metric not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PutMapping("/{id}")
  ResponseEntity<HealthMetricResponseDTO> updateHealthMetric(
      @Parameter(description = "Health metric unique identifier") @PathVariable UUID id,
      @Parameter(hidden = true) @RequestAttribute UUID userId,
      @Valid @RequestBody UpdateHealthMetricRequestDTO request);

  @Operation(
      operationId = "deleteHealthMetric",
      summary = "Delete health metric",
      description = "Deletes an existing health metric",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Health metric deleted successfully"),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - not your metric",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Health metric not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteHealthMetric(
      @Parameter(description = "Health metric unique identifier") @PathVariable UUID id,
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "getRecentHealthMetrics",
      summary = "Get recent health metrics",
      description = "Returns health metrics from the last 90 days",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of recent health metrics",
        content =
            @Content(
                array =
                    @ArraySchema(
                        schema = @Schema(implementation = HealthMetricResponseDTO.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/recent")
  ResponseEntity<List<HealthMetricResponseDTO>> getRecentHealthMetrics(
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "getLatestHealthScore",
      summary = "Get current health score",
      description = "Returns the most recent health score for the user",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Current health score",
        content = @Content(schema = @Schema(implementation = HealthScoreResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "No health score found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/score")
  ResponseEntity<HealthScoreResponseDTO> getLatestHealthScore(
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "getHealthScoreHistory",
      summary = "Get health score history",
      description = "Returns historical health scores for the user",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of historical health scores",
        content =
            @Content(
                array =
                    @ArraySchema(schema = @Schema(implementation = HealthScoreResponseDTO.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/score/history")
  ResponseEntity<List<HealthScoreResponseDTO>> getHealthScoreHistory(
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "calculateHealthScore",
      summary = "Calculate health score",
      description = "Calculates a new health score based on recent metrics",
      security = {@SecurityRequirement(name = "Bearer Auth")})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Health score calculated successfully",
        content = @Content(schema = @Schema(implementation = HealthScoreResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Insufficient data to calculate score",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PostMapping("/score/calculate")
  ResponseEntity<HealthScoreResponseDTO> calculateHealthScore(
      @Parameter(hidden = true) @RequestAttribute UUID userId, UriComponentsBuilder uriBuilder);
}
