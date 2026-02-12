package dev.jose.healflow_api.persistence.repositories;

import dev.jose.healflow_api.persistence.entities.SpecialistAvailabilityEntity;
import dev.jose.healflow_api.persistence.entities.SpecialistEntity;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialistAvailabilityRepository
    extends JpaRepository<SpecialistAvailabilityEntity, UUID> {
  List<SpecialistAvailabilityEntity> findBySpecialistAndIsAvailableTrue(
      SpecialistEntity specialist);

  List<SpecialistAvailabilityEntity> findBySpecialistIdAndDayOfWeekAndIsAvailableTrue(
      UUID specialistId, DayOfWeek dayOfWeek);
}
