package dev.jose.healflow_api.persistence.repositories;

import dev.jose.healflow_api.enumerations.AppointmentStatus;
import dev.jose.healflow_api.persistence.entities.AppointmentEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {

  List<AppointmentEntity> findByClientIdOrderByAppointmentDateDesc(UUID clientId);

  List<AppointmentEntity> findBySpecialistIdOrderByAppointmentDateDesc(UUID specialistId);

  @Query(
      "SELECT a FROM AppointmentEntity a WHERE a.client.authId = :clientId "
          + "AND a.appointmentDate >= :now AND a.status IN :statuses "
          + "ORDER BY a.appointmentDate ASC")
  List<AppointmentEntity> findUpcomingByClientId(
      @Param("clientId") UUID clientId,
      @Param("now") Instant now,
      @Param("statuses") List<AppointmentStatus> statuses);

  @Query(
      "SELECT a FROM AppointmentEntity a WHERE a.client.authId = :authId "
          + "AND a.appointmentDate < :now "
          + "ORDER BY a.appointmentDate DESC")
  List<AppointmentEntity> findPastByAuthId(@Param("authId") UUID authId, @Param("now") Instant now);

  @Query(
      "SELECT a FROM AppointmentEntity a WHERE a.specialist.id = :specialistId "
          + "AND a.appointmentDate >= :startDate AND a.appointmentDate < :endDate "
          + "AND a.status NOT IN ('CANCELLED')")
  List<AppointmentEntity> findBySpecialistAndDateRange(
      @Param("specialistId") UUID specialistId,
      @Param("startDate") Instant startDate,
      @Param("endDate") Instant endDate);

  @Query(
      "SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AppointmentEntity a "
          + "WHERE a.specialist.id = :specialistId "
          + "AND a.appointmentDate = :appointmentDate "
          + "AND a.status NOT IN ('CANCELLED') "
          + "AND (:excludeId IS NULL OR a.id <> :excludeId)")
  boolean existsConflictingAppointment(
      @Param("specialistId") UUID specialistId,
      @Param("appointmentDate") Instant appointmentDate,
      @Param("excludeId") UUID excludeId);
}
