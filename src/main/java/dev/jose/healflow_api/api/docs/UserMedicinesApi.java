package dev.jose.healflow_api.api.docs;

import dev.jose.healflow_api.api.models.AddMedicineToUserRequestDTO;
import dev.jose.healflow_api.api.models.UpdateUserMedicineRequestDTO;
import dev.jose.healflow_api.api.models.UserMedicineCountResponseDTO;
import dev.jose.healflow_api.api.models.UserMedicinesResponseDTO;
import dev.jose.healflow_api.api.models.errors.ApiProblemDetail;
import dev.jose.healflow_api.api.models.errors.ValidationProblemDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "User Medicines", description = "User medicines management API")
@RequestMapping("/user-medicines")
public interface UserMedicinesApi {

  @Operation(
      operationId = "getUserMedicines",
      summary = "Get user medicines",
      description = "Returns list of medicines for a user",
      security = {@SecurityRequirement(name = "Bearer Auth")},
      tags = {"User Medicines"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "List of medicines",
        content =
            @io.swagger.v3.oas.annotations.media.Content(
                array =
                    @io.swagger.v3.oas.annotations.media.ArraySchema(
                        schema =
                            @io.swagger.v3.oas.annotations.media.Schema(
                                implementation = UserMedicinesResponseDTO.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content =
            @io.swagger.v3.oas.annotations.media.Content(
                schema =
                    @io.swagger.v3.oas.annotations.media.Schema(
                        implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "User not found",
        content =
            @io.swagger.v3.oas.annotations.media.Content(
                schema =
                    @io.swagger.v3.oas.annotations.media.Schema(
                        implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content =
            @io.swagger.v3.oas.annotations.media.Content(
                schema =
                    @io.swagger.v3.oas.annotations.media.Schema(
                        implementation = ApiProblemDetail.class)))
  })
  @GetMapping
  ResponseEntity<List<UserMedicinesResponseDTO>> getUserMedicines(
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "getUserMedicineById",
      summary = "Get user medicine by ID",
      description = "Returns a specific medicine for the authenticated user",
      security = {@SecurityRequirement(name = "Bearer Auth")},
      tags = {"User Medicines"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Medicine details",
        content = @Content(schema = @Schema(implementation = UserMedicinesResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - User doesn't own this medicine",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Medicine not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/{userId}/{medicineId}")
  ResponseEntity<UserMedicinesResponseDTO> getUserMedicineById(
      @Parameter(description = "User ID", required = true) @PathVariable UUID userId,
      @Parameter(description = "Medicine ID", required = true) @PathVariable Integer medicineId,
      @Parameter(hidden = true) @RequestAttribute UUID requestUserId);

  @Operation(
      operationId = "getUserMedicineCount",
      summary = "Get user medicine count",
      description = "Returns the count of medicines for the authenticated user",
      security = {@SecurityRequirement(name = "Bearer Auth")},
      tags = {"User Medicines"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Medicine count",
        content = @Content(schema = @Schema(implementation = UserMedicineCountResponseDTO.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @GetMapping("/count")
  ResponseEntity<UserMedicineCountResponseDTO> getUserMedicineCount(
      @Parameter(hidden = true) @RequestAttribute UUID userId);

  @Operation(
      operationId = "addMedicineToUser",
      summary = "Add medicine to user",
      description = "Adds a new medicine to the authenticated user's prescription",
      security = {@SecurityRequirement(name = "Bearer Auth")},
      tags = {"User Medicines"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Medicine added successfully",
        content = @Content(schema = @Schema(implementation = UserMedicinesResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = ValidationProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "User or medicine not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PostMapping
  ResponseEntity<UserMedicinesResponseDTO> addMedicineToUser(
      @Parameter(hidden = true) @RequestAttribute UUID userId,
      @Valid @RequestBody AddMedicineToUserRequestDTO request,
      UriComponentsBuilder uriBuilder);

  @Operation(
      operationId = "updateUserMedicine",
      summary = "Update user medicine",
      description = "Updates an existing medicine prescription for the authenticated user",
      security = {@SecurityRequirement(name = "Bearer Auth")},
      tags = {"User Medicines"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Medicine updated successfully",
        content = @Content(schema = @Schema(implementation = UserMedicinesResponseDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid input",
        content = @Content(schema = @Schema(implementation = ValidationProblemDetail.class))),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - User doesn't own this medicine",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Medicine not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @PutMapping("/{userId}/{medicineId}")
  ResponseEntity<UserMedicinesResponseDTO> updateUserMedicine(
      @Parameter(description = "User ID", required = true) @PathVariable UUID userId,
      @Parameter(description = "Medicine ID", required = true) @PathVariable Integer medicineId,
      @Parameter(hidden = true) @RequestAttribute UUID requestUserId,
      @Valid @RequestBody UpdateUserMedicineRequestDTO request);

  @Operation(
      operationId = "deleteUserMedicine",
      summary = "Delete user medicine",
      description = "Removes a medicine from the authenticated user's prescription",
      security = {@SecurityRequirement(name = "Bearer Auth")},
      tags = {"User Medicines"})
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Medicine deleted successfully"),
    @ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "403",
        description = "Forbidden - User doesn't own this medicine",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Medicine not found",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ApiProblemDetail.class)))
  })
  @DeleteMapping("/{userId}/{medicineId}")
  ResponseEntity<Void> deleteUserMedicine(
      @Parameter(description = "User ID", required = true) @PathVariable UUID userId,
      @Parameter(description = "Medicine ID", required = true) @PathVariable Integer medicineId,
      @Parameter(hidden = true) @RequestAttribute UUID requestUserId);
}
