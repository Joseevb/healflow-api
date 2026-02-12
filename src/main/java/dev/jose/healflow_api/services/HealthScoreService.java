package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.HealthScoreResponseDTO;
import java.util.List;
import java.util.UUID;

public interface HealthScoreService {

  /**
   * Get the latest health score for a user
   *
   * @param userId User unique identifier
   * @return Health score with details
   */
  HealthScoreResponseDTO getLatestHealthScore(UUID userId);

  /**
   * Get health score history for a user
   *
   * @param userId User unique identifier
   * @return List of health scores
   */
  List<HealthScoreResponseDTO> getHealthScoreHistory(UUID userId);

  /**
   * Calculate and save a new health score for a user
   *
   * @param userId User unique identifier
   * @return Calculated health score
   */
  HealthScoreResponseDTO calculateHealthScore(UUID userId);

  /**
   * Recalculate health score (triggered after new metrics are added)
   *
   * @param userId User unique identifier
   */
  void recalculateHealthScoreAsync(UUID userId);
}
