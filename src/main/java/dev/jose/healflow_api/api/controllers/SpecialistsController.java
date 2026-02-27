package dev.jose.healflow_api.api.controllers;

import dev.jose.healflow_api.api.docs.SpecialistsApi;
import dev.jose.healflow_api.api.models.CreateSpecialistRequestDTO;
import dev.jose.healflow_api.api.models.DayScheduleResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistAvailabilityResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistTypeResponseDTO;
import dev.jose.healflow_api.api.models.UpdateSpecialistAvailabilityRequestDTO;
import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import dev.jose.healflow_api.exceptions.ForbiddenException;
import dev.jose.healflow_api.services.SpecialistService;
import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SpecialistsController implements SpecialistsApi {

  private final SpecialistService specialistService;

  @Override
  public ResponseEntity<List<SpecialistResponseDTO>> getAvailableSpecialists(
      Optional<SpecialistTypeEnum> type) {
    return ResponseEntity.ok(specialistService.getAvailableSpecialists(type));
  }

  @Override
  public ResponseEntity<List<SpecialistTypeResponseDTO>> getSpecialistTypes() {
    return ResponseEntity.ok(
        Arrays.asList(SpecialistTypeEnum.values()).stream()
            .map(SpecialistTypeResponseDTO::new)
            .toList());
  }

  @Override
  public ResponseEntity<List<DayScheduleResponseDTO>> getSpecialistBookingData(
      UUID specialistId, Instant startDate, Instant endDate) {
    return ResponseEntity.ok(
        specialistService.getSpecialistBookingData(specialistId, startDate, endDate));
  }

  @Override
  public ResponseEntity<SpecialistResponseDTO> createSpecialist(
      CreateSpecialistRequestDTO request) {
    return ResponseEntity.status(201).body(specialistService.createSpecialist(request));
  }

  @Override
  public ResponseEntity<List<SpecialistAvailabilityResponseDTO>> getSpecialistAvailabilities(
      UUID specialistId) {
    return ResponseEntity.ok(specialistService.getSpecialistAvailabilities(specialistId));
  }

  @Override
  public ResponseEntity<SpecialistAvailabilityResponseDTO> updateSpecialistAvailability(
      UUID specialistId, DayOfWeek dayOfWeek, UpdateSpecialistAvailabilityRequestDTO request) {
    validateSpecialistAccess(specialistId);
    return ResponseEntity.ok(
        specialistService.updateAvailability(specialistId, dayOfWeek, request));
  }

  @Override
  public ResponseEntity<Void> deleteSpecialistAvailability(UUID specialistId, UUID availabilityId) {
    validateSpecialistAccess(specialistId);
    specialistService.deleteAvailability(specialistId, availabilityId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Validates that the authenticated specialist can only manage their own timeslots.
   *
   * @param specialistId The specialist ID from the request path
   * @throws ForbiddenException if the specialist tries to access another specialist's data
   */
  private void validateSpecialistAccess(UUID specialistId) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();
      String specialistIdStr = jwt.getClaimAsString("specialist_id");

      if (specialistIdStr != null) {
        UUID authSpecialistId = UUID.fromString(specialistIdStr);
        if (!authSpecialistId.equals(specialistId)) {
          throw new ForbiddenException(
              "Specialists can only manage their own availability timeslots");
        }
      } else {
        throw new ForbiddenException("Authenticated user is not a specialist");
      }
    }
  }
}
