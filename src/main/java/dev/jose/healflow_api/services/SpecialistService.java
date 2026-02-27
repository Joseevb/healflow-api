package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.CreateSpecialistRequestDTO;
import dev.jose.healflow_api.api.models.DayScheduleResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistAvailabilityResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistResponseDTO;
import dev.jose.healflow_api.api.models.UpdateSpecialistAvailabilityRequestDTO;
import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import java.time.DayOfWeek;
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

  /**
   * Creates a new specialist in the system
   *
   * @param request The specialist creation request
   * @return The created specialist response
   */
  SpecialistResponseDTO createSpecialist(CreateSpecialistRequestDTO request);

  /**
   * Gets all availability timeslots for a specialist
   *
   * @param specialistId Specialist unique identifier
   * @return List of availability timeslots
   */
  List<SpecialistAvailabilityResponseDTO> getSpecialistAvailabilities(UUID specialistId);

  /**
   * Updates availability timeslot for a specialist
   *
   * @param specialistId Specialist unique identifier
   * @param dayOfWeek Day of week to update
   * @param request The availability update request
   * @return The updated availability
   */
  SpecialistAvailabilityResponseDTO updateAvailability(
      UUID specialistId, DayOfWeek dayOfWeek, UpdateSpecialistAvailabilityRequestDTO request);

  /**
   * Deletes availability timeslot for a specialist
   *
   * @param specialistId Specialist unique identifier
   * @param availabilityId Availability record unique identifier
   */
  void deleteAvailability(UUID specialistId, UUID availabilityId);
}
