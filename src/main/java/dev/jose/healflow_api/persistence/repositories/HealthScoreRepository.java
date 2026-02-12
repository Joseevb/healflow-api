package dev.jose.healflow_api.persistence.repositories;

import dev.jose.healflow_api.persistence.entities.HealthScoreEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthScoreRepository extends JpaRepository<HealthScoreEntity, UUID> {

  /**
   * Find the latest health score for a user
   *
   * @param userId User unique identifier
   * @return Optional health score
   */
  @Query(
      "SELECT hs FROM HealthScoreEntity hs WHERE hs.user.authId = :userId "
          + "ORDER BY hs.calculatedAt DESC LIMIT 1")
  Optional<HealthScoreEntity> findLatestByUserId(@Param("userId") UUID userId);

  /**
   * Find all health scores for a user ordered by calculation date descending
   *
   * @param userId User unique identifier
   * @return List of health scores
   */
  @Query(
      "SELECT hs FROM HealthScoreEntity hs WHERE hs.user.authId = :userId "
          + "ORDER BY hs.calculatedAt DESC")
  List<HealthScoreEntity> findByUserIdOrderByCalculatedAtDesc(@Param("userId") UUID userId);

  /**
   * Find health scores for a user with pagination
   *
   * @param userId User unique identifier
   * @return List of health scores
   */
  List<HealthScoreEntity> findTop10ByUserAuthIdOrderByCalculatedAtDesc(UUID userId);
}
