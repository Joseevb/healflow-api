package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.AppointmentResponseDTO;
import dev.jose.healflow_api.api.models.CreateAppointmentRequestDTO;
import dev.jose.healflow_api.api.models.UpdateAppointmentRequestDTO;
import dev.jose.healflow_api.enumerations.AppointmentStatus;
import dev.jose.healflow_api.exceptions.ConflictException;
import dev.jose.healflow_api.exceptions.ForbiddenException;
import dev.jose.healflow_api.exceptions.NotFoundException;
import dev.jose.healflow_api.exceptions.ValidationException;
import dev.jose.healflow_api.mappers.AppointmentMapper;
import dev.jose.healflow_api.persistence.entities.AppointmentEntity;
import dev.jose.healflow_api.persistence.entities.SpecialistAvailabilityEntity;
import dev.jose.healflow_api.persistence.entities.SpecialistEntity;
import dev.jose.healflow_api.persistence.entities.UserEntity;
import dev.jose.healflow_api.persistence.repositories.AppointmentRepository;
import dev.jose.healflow_api.persistence.repositories.SpecialistAvailabilityRepository;
import dev.jose.healflow_api.persistence.repositories.SpecialistRepository;
import dev.jose.healflow_api.persistence.repositories.UserRepository;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

  private final UserRepository userRepository;
  private final AppointmentMapper appointmentMapper;
  private final SpecialistRepository specialistRepository;
  private final AppointmentRepository appointmentRepository;
  private final SpecialistAvailabilityRepository availabilityRepository;

  @Override
  @Transactional(readOnly = true)
  public List<AppointmentResponseDTO> getUserAppointments(UUID userId) {
    log.info("Fetching all appointments for user: {}", userId);
    return appointmentRepository.findByClientIdOrderByAppointmentDateDesc(userId).stream()
        .map(appointmentMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<AppointmentResponseDTO> getUpcomingAppointments(UUID userId) {
    log.debug("Fetching upcoming appointments for user: {}", userId);
    Instant now = Instant.now();
    List<AppointmentStatus> activeStatuses =
        List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED);

    return appointmentRepository.findUpcomingByClientId(userId, now, activeStatuses).stream()
        .map(appointmentMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<AppointmentResponseDTO> getPastAppointments(UUID userId) {
    log.debug("Fetching past appointments for user: {}", userId);
    Instant now = Instant.now();
    return appointmentRepository.findPastByAuthId(userId, now).stream()
        .map(appointmentMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public AppointmentResponseDTO getAppointmentById(UUID id, UUID userId) {
    log.debug("Fetching appointment: {} for user: {}", id, userId);

    AppointmentEntity appointment =
        appointmentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Appointment", "id", id));

    if (!appointment.getClient().getId().equals(userId)) {
      throw new ForbiddenException("You don't have permission to access this appointment");
    }

    return appointmentMapper.toDto(appointment);
  }

  @Override
  @Transactional
  public AppointmentResponseDTO createAppointment(
      UUID userId, CreateAppointmentRequestDTO request) {
    log.info(
        "Creating appointment for user: {} with specialist: {}", userId, request.specialistId());

    UserEntity user =
        userRepository
            .findByAuthId(userId)
            .orElseThrow(() -> new NotFoundException("User", "id", userId));

    SpecialistEntity specialist =
        specialistRepository
            .findById(request.specialistId())
            .orElseThrow(() -> new NotFoundException("Specialist", "id", request.specialistId()));

    if (!specialist.getIsActive()) {
      throw new ValidationException("Specialist is not currently accepting appointments");
    }

    // Validate appointment date is within business hours
    validateBusinessHours(specialist, request.appointmentDate());

    if (appointmentRepository.existsConflictingAppointment(
        request.specialistId(), request.appointmentDate(), null)) {
      throw new ConflictException("This time slot is already booked");
    }

    AppointmentEntity appointment = appointmentMapper.toEntity(request, user, specialist);

    appointment = appointmentRepository.save(appointment);
    log.info("Successfully created appointment: {}", appointment.getId());

    return appointmentMapper.toDto(appointment);
  }

  @Override
  @Transactional
  public AppointmentResponseDTO updateAppointment(
      UUID id, UUID userId, UpdateAppointmentRequestDTO request) {
    log.info("Updating appointment: {} for user: {}", id, userId);

    AppointmentEntity appointment =
        appointmentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Appointment", "id", id));

    // Verify ownership
    if (!appointment.getClient().getId().equals(userId)) {
      throw new ForbiddenException("You don't have permission to update this appointment");
    }

    // Validate appointment is not in the past or completed
    if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
      throw new ValidationException("Cannot update a completed appointment");
    }

    // If changing date, validate new date
    if (request.appointmentDate() != null
        && !request.appointmentDate().equals(appointment.getAppointmentDate())) {

      if (request.appointmentDate().isBefore(Instant.now())) {
        throw new ValidationException("New appointment date must be in the future");
      }

      validateBusinessHours(appointment.getSpecialist(), request.appointmentDate());

      if (appointmentRepository.existsConflictingAppointment(
          appointment.getSpecialist().getId(), request.appointmentDate(), appointment.getId())) {
        throw new ConflictException("The new time slot is already booked");
      }
    }

    // Validate cancellation
    if (request.status() == AppointmentStatus.CANCELLED
        && (request.cancellationReason() == null || request.cancellationReason().isBlank())) {
      throw new ValidationException("Cancellation reason is required");
    }

    appointment = appointmentMapper.updateEntity(appointment, request);
    appointment = appointmentRepository.save(appointment);

    log.info("Successfully updated appointment: {}", id);
    return appointmentMapper.toDto(appointment);
  }

  @Override
  @Transactional
  public void cancelAppointment(UUID id, UUID userId, String reason) {
    log.info("Cancelling appointment: {} for user: {}", id, userId);

    AppointmentEntity appointment =
        appointmentRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Appointment", "id", id));

    // Verify ownership
    if (!appointment.getClient().getId().equals(userId)) {
      throw new ForbiddenException("You don't have permission to cancel this appointment");
    }

    if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
      throw new ValidationException("Cannot cancel a completed appointment");
    }

    if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
      throw new ValidationException("Appointment is already cancelled");
    }

    appointment.setStatus(AppointmentStatus.CANCELLED);
    appointment.setCancellationReason(reason);
    appointmentRepository.save(appointment);

    log.info("Successfully cancelled appointment: {}", id);
  }

  private void validateBusinessHours(SpecialistEntity specialist, Instant appointmentDate) {
    LocalDateTime dateTime = LocalDateTime.ofInstant(appointmentDate, ZoneId.systemDefault());
    DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
    LocalTime time = dateTime.toLocalTime();

    // Check if specialist works on this day
    List<SpecialistAvailabilityEntity> dayAvailabilities =
        availabilityRepository.findBySpecialistIdAndDayOfWeekAndIsAvailableTrue(
            specialist.getId(), dayOfWeek);

    if (dayAvailabilities.isEmpty()) {
      throw new ValidationException("Specialist is not available on " + dayOfWeek);
    }

    // Check if time falls within any availability window
    boolean withinHours =
        dayAvailabilities.stream()
            .anyMatch(a -> !time.isBefore(a.getStartTime()) && time.isBefore(a.getEndTime()));

    if (!withinHours) {
      throw new ValidationException("Appointment time is outside specialist's working hours");
    }
  }
}
