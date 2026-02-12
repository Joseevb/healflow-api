package dev.jose.healflow_api.persistence.repositories;

import dev.jose.healflow_api.persistence.entities.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findByAuthId(UUID authId);

  boolean existsByEmail(String email);

  boolean existsByAuthId(UUID authId);
}
