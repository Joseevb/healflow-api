package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.DayScheduleResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistResponseDTO;
import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecialistService {

  /**
   * Returns available specialists
   *
   * @return List of active specialists
   */
  List<SpecialistResponseDTO> getAvailableSpecialists(Optional<SpecialistTypeEnum> type);

  /**
   * Returns booking data for a specific specialist
   *
   * @param specialistId Specialist unique identifier
   * @param startDate Start date for the schedule
   * @param endDate End date for the schedule
   * @return List of day schedules
   */
  List<DayScheduleResponseDTO> getSpecialistBookingData(
      UUID specialistId, Instant startDate, Instant endDate);
}
