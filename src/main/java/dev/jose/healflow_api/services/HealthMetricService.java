package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.*;
import dev.jose.healflow_api.enumerations.HealthMetricType;
import java.util.List;
import java.util.UUID;

public interface HealthMetricService {

  /**
   * Get all health metrics for a user
   *
   * @param userId User unique identifier
   * @return List of health metrics
   */
  List<HealthMetricResponseDTO> getUserHealthMetrics(UUID userId);

  /**
   * Get filtered health metrics for a user
   *
   * @param userId User unique identifier
   * @param filter Filter parameters
   * @return List of filtered health metrics
   */
  List<HealthMetricResponseDTO> getFilteredHealthMetrics(
      UUID userId, HealthMetricFilterRequestDTO filter);

  /**
   * Get a specific health metric by ID
   *
   * @param id Health metric unique identifier
   * @param userId User ID to verify ownership
   * @return Health metric details
   */
  HealthMetricResponseDTO getHealthMetricById(UUID id, UUID userId);

  /**
   * Get latest metric of a specific type
   *
   * @param userId User unique identifier
   * @param metricType Type of health metric
   * @return Health metric details
   */
  HealthMetricResponseDTO getLatestMetricByType(UUID userId, HealthMetricType metricType);

  /**
   * Create a new health metric
   *
   * @param userId User unique identifier
   * @param request Health metric creation details
   * @return Created health metric
   */
  HealthMetricResponseDTO createHealthMetric(UUID userId, CreateHealthMetricRequestDTO request);

  /**
   * Create multiple health metrics at once
   *
   * @param userId User unique identifier
   * @param request Batch creation request
   * @return List of created health metrics
   */
  List<HealthMetricResponseDTO> createHealthMetricsBatch(
      UUID userId, BatchCreateHealthMetricsRequestDTO request);

  /**
   * Update an existing health metric
   *
   * @param id Health metric unique identifier
   * @param userId User ID to verify ownership
   * @param request Update details
   * @return Updated health metric
   */
  HealthMetricResponseDTO updateHealthMetric(
      UUID id, UUID userId, UpdateHealthMetricRequestDTO request);

  /**
   * Delete a health metric
   *
   * @param id Health metric unique identifier
   * @param userId User ID to verify ownership
   */
  void deleteHealthMetric(UUID id, UUID userId);

  /**
   * Get health metrics for the last 90 days
   *
   * @param userId User unique identifier
   * @return List of health metrics
   */
  List<HealthMetricResponseDTO> getRecentHealthMetrics(UUID userId);
}
