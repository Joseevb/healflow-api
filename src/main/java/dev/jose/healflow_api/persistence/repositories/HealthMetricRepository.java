package dev.jose.healflow_api.persistence.repositories;

import dev.jose.healflow_api.enumerations.HealthMetricType;
import dev.jose.healflow_api.persistence.entities.HealthMetricEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthMetricRepository extends JpaRepository<HealthMetricEntity, UUID> {

  /**
   * Find all health metrics for a user ordered by recorded date descending
   *
   * @param userId User unique identifier
   * @return List of health metrics
   */
  List<HealthMetricEntity> findByUserAuthIdOrderByRecordedAtDesc(UUID userId);

  /**
   * Find health metrics by user and metric type
   *
   * @param userId User unique identifier
   * @param metricType Type of health metric
   * @return List of health metrics
   */
  List<HealthMetricEntity> findByUserAuthIdAndMetricTypeOrderByRecordedAtDesc(
      UUID userId, HealthMetricType metricType);

  /**
   * Find health metrics within a date range
   *
   * @param userId User unique identifier
   * @param start Start date
   * @param end End date
   * @return List of health metrics
   */
  @Query(
      "SELECT hm FROM HealthMetricEntity hm WHERE hm.user.authId = :userId "
          + "AND hm.recordedAt BETWEEN :start AND :end ORDER BY hm.recordedAt DESC")
  List<HealthMetricEntity> findByUserIdAndRecordedAtBetween(
      @Param("userId") UUID userId, @Param("start") Instant start, @Param("end") Instant end);

  /**
   * Find latest metric of a specific type for a user
   *
   * @param userId User unique identifier
   * @param metricType Type of health metric
   * @return Optional health metric
   */
  @Query(
      "SELECT hm FROM HealthMetricEntity hm WHERE hm.user.authId = :userId "
          + "AND hm.metricType = :metricType ORDER BY hm.recordedAt DESC LIMIT 1")
  Optional<HealthMetricEntity> findLatestByUserIdAndMetricType(
      @Param("userId") UUID userId, @Param("metricType") HealthMetricType metricType);

  /**
   * Count metrics for a user after a specific date
   *
   * @param userId User unique identifier
   * @param after Date threshold
   * @return Count of metrics
   */
  @Query(
      "SELECT COUNT(hm) FROM HealthMetricEntity hm WHERE hm.user.authId = :userId "
          + "AND hm.recordedAt >= :after")
  Long countByUserIdAndRecordedAtAfter(@Param("userId") UUID userId, @Param("after") Instant after);

  /**
   * Find metrics by user, type, and date range
   *
   * @param userId User unique identifier
   * @param metricType Type of health metric
   * @param start Start date
   * @param end End date
   * @return List of health metrics
   */
  @Query(
      "SELECT hm FROM HealthMetricEntity hm WHERE hm.user.authId = :userId "
          + "AND hm.metricType = :metricType AND hm.recordedAt BETWEEN :start AND :end "
          + "ORDER BY hm.recordedAt DESC")
  List<HealthMetricEntity> findByUserIdAndMetricTypeAndDateRange(
      @Param("userId") UUID userId,
      @Param("metricType") HealthMetricType metricType,
      @Param("start") Instant start,
      @Param("end") Instant end);

  /**
   * Find all metrics for a user after a specific date
   *
   * @param userId User unique identifier
   * @param after Date threshold
   * @return List of health metrics
   */
  @Query(
      "SELECT hm FROM HealthMetricEntity hm WHERE hm.user.authId = :userId "
          + "AND hm.recordedAt >= :after ORDER BY hm.recordedAt DESC")
  List<HealthMetricEntity> findByUserIdAndRecordedAtAfter(
      @Param("userId") UUID userId, @Param("after") Instant after);
}
