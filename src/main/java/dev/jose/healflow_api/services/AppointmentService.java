package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.*;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {

  /**
   * Returns all appointments for the authenticated user
   *
   * @param userId User unique identifier
   * @return List of appointment responses
   */
  List<AppointmentResponseDTO> getUserAppointments(UUID userId);

  /**
   * Returns upcoming appointments for the authenticated user
   *
   * @param userId User unique identifier
   * @return List of upcoming appointment responses
   */
  List<AppointmentResponseDTO> getUpcomingAppointments(UUID userId);

  /**
   * Returns past appointments for the authenticated user
   *
   * @param userId User unique identifier
   * @return List of past appointment responses
   */
  List<AppointmentResponseDTO> getPastAppointments(UUID userId);

  /**
   * Returns an appointment by its unique identifier
   *
   * @param id Appointment unique identifier
   * @param userId User ID to verify ownership
   * @return Appointment response
   */
  AppointmentResponseDTO getAppointmentById(UUID id, UUID userId);

  /**
   * Creates a new appointment
   *
   * @param userId User ID making the appointment
   * @param request Appointment creation details
   * @return Created appointment response
   */
  AppointmentResponseDTO createAppointment(UUID userId, CreateAppointmentRequestDTO request);

  /**
   * Updates an existing appointment
   *
   * @param id Appointment unique identifier
   * @param userId User ID to verify ownership
   * @param request Update details
   * @return Updated appointment response
   */
  AppointmentResponseDTO updateAppointment(
      UUID id, UUID userId, UpdateAppointmentRequestDTO request);

  /**
   * Cancels an appointment
   *
   * @param id Appointment unique identifier
   * @param userId User ID to verify ownership
   * @param reason Cancellation reason
   */
  void cancelAppointment(UUID id, UUID userId, String reason);
}
