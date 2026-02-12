package dev.jose.healflow_api.api.controllers;

import dev.jose.healflow_api.api.docs.SpecialistsApi;
import dev.jose.healflow_api.api.models.DayScheduleResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistTypeResponseDTO;
import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import dev.jose.healflow_api.services.SpecialistService;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
