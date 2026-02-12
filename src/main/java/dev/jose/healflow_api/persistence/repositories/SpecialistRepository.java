package dev.jose.healflow_api.persistence.repositories;

import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import dev.jose.healflow_api.persistence.entities.SpecialistEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialistRepository extends JpaRepository<SpecialistEntity, UUID> {

  Optional<SpecialistEntity> findByEmail(String email);

  List<SpecialistEntity> findByIsActiveTrue();

  @Query("SELECT s FROM SpecialistEntity s WHERE s.specialty = :type AND s.isActive = true")
  List<SpecialistEntity> findActiveBySpecialistType(@Param("type") SpecialistTypeEnum type);
}
