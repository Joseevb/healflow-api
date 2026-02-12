package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.DayScheduleResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistResponseDTO;
import dev.jose.healflow_api.api.models.TimeSlotDTO;
import dev.jose.healflow_api.enumerations.AppointmentStatus;
import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import dev.jose.healflow_api.exceptions.NotFoundException;
import dev.jose.healflow_api.mappers.SpecialistMapper;
import dev.jose.healflow_api.persistence.entities.AppointmentEntity;
import dev.jose.healflow_api.persistence.entities.SpecialistAvailabilityEntity;
import dev.jose.healflow_api.persistence.entities.SpecialistEntity;
import dev.jose.healflow_api.persistence.repositories.AppointmentRepository;
import dev.jose.healflow_api.persistence.repositories.SpecialistAvailabilityRepository;
import dev.jose.healflow_api.persistence.repositories.SpecialistRepository;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpecialistServiceImpl implements SpecialistService {

  private final SpecialistMapper specialistMapper;
  private final SpecialistRepository specialistRepository;
  private final AppointmentRepository appointmentRepository;
  private final SpecialistAvailabilityRepository availabilityRepository;

  @Override
  @Transactional(readOnly = true)
  public List<SpecialistResponseDTO> getAvailableSpecialists(Optional<SpecialistTypeEnum> type) {
    return type.map(specialistRepository::findActiveBySpecialistType)
        .map(specialist -> specialist.stream().map(specialistMapper::toDto).toList())
        .orElse(
            specialistRepository.findByIsActiveTrue().stream()
                .map(specialistMapper::toDto)
                .toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<DayScheduleResponseDTO> getSpecialistBookingData(
      UUID specialistId, Instant startDate, Instant endDate) {
    log.debug(
        "Fetching booking data for specialist: {} from {} to {}", specialistId, startDate, endDate);

    SpecialistEntity specialist =
        specialistRepository
            .findById(specialistId)
            .orElseThrow(() -> new NotFoundException("Specialist", "id", specialistId));

    // Get all appointments in the date range
    List<AppointmentEntity> appointments =
        appointmentRepository.findBySpecialistAndDateRange(specialistId, startDate, endDate);

    // Get specialist availabilities
    var availabilities = availabilityRepository.findBySpecialistAndIsAvailableTrue(specialist);

    // Build schedule
    List<DayScheduleResponseDTO> schedules = new ArrayList<>();
    Instant current = startDate;

    while (current.isBefore(endDate)) {
      LocalDate localDate = LocalDate.ofInstant(current, ZoneId.systemDefault());
      DayOfWeek dayOfWeek = localDate.getDayOfWeek();

      // Skip weekends
      if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
        current = current.plus(1, ChronoUnit.DAYS);
        continue;
      }

      // Get availability for this day
      var dayAvailabilities =
          availabilities.stream().filter(a -> a.getDayOfWeek() == dayOfWeek).toList();

      if (!dayAvailabilities.isEmpty()) {
        List<TimeSlotDTO> timeSlots =
            generateTimeSlots(current, dayAvailabilities, appointments, specialist);
        schedules.add(DayScheduleResponseDTO.builder().date(current).timeslots(timeSlots).build());
      }

      current = current.plus(1, ChronoUnit.DAYS);
    }

    return schedules;
  }

  private List<TimeSlotDTO> generateTimeSlots(
      Instant date,
      List<SpecialistAvailabilityEntity> availabilities,
      List<AppointmentEntity> appointments,
      SpecialistEntity specialist) {

    List<TimeSlotDTO> slots = new ArrayList<>();
    LocalDate localDate = LocalDate.ofInstant(date, ZoneId.systemDefault());

    for (var availability : availabilities) {
      LocalTime current = availability.getStartTime();
      LocalTime end = availability.getEndTime();

      while (current.isBefore(end)) {
        LocalDateTime slotDateTime = LocalDateTime.of(localDate, current);
        Instant slotInstant = slotDateTime.atZone(ZoneId.systemDefault()).toInstant();

        // Check if this slot is booked
        Optional<AppointmentEntity> bookedAppointment =
            appointments.stream()
                .filter(a -> a.getAppointmentDate().equals(slotInstant))
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .findFirst();

        TimeSlotDTO slot;
        if (bookedAppointment.isPresent()) {
          slot =
              TimeSlotDTO.builder()
                  .time(current.toString())
                  .status("booked")
                  .appointmentId(bookedAppointment.get().getId())
                  .build();
        } else {
          slot = TimeSlotDTO.builder().time(current.toString()).status("available").build();
        }

        slots.add(slot);
        current = current.plusMinutes(specialist.getConsultationDurationMinutes());
      }
    }

    return slots;
  }
}
