package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.*;
import dev.jose.healflow_api.enumerations.HealthMetricType;
import dev.jose.healflow_api.exceptions.ForbiddenException;
import dev.jose.healflow_api.exceptions.NotFoundException;
import dev.jose.healflow_api.mappers.HealthMetricMapper;
import dev.jose.healflow_api.persistence.entities.HealthMetricEntity;
import dev.jose.healflow_api.persistence.entities.UserEntity;
import dev.jose.healflow_api.persistence.repositories.HealthMetricRepository;
import dev.jose.healflow_api.persistence.repositories.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthMetricServiceImpl implements HealthMetricService {

  private final HealthMetricRepository healthMetricRepository;
  private final UserRepository userRepository;
  private final HealthMetricMapper healthMetricMapper;

  @Override
  @Transactional(readOnly = true)
  public List<HealthMetricResponseDTO> getUserHealthMetrics(UUID userId) {
    log.debug("Fetching all health metrics for user: {}", userId);
    return healthMetricRepository.findByUserAuthIdOrderByRecordedAtDesc(userId).stream()
        .map(healthMetricMapper::toDto)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<HealthMetricResponseDTO> getFilteredHealthMetrics(
      UUID userId, HealthMetricFilterRequestDTO filter) {
    log.debug("Fetching filtered health metrics for user: {}", userId);

    List<HealthMetricEntity> metrics;

    if (filter.metricType() != null && filter.startDate() != null && filter.endDate() != null) {
      metrics =
          healthMetricRepository.findByUserIdAndMetricTypeAndDateRange(
              userId, filter.metricType(), filter.startDate(), filter.endDate());
    } else if (filter.metricType() != null) {
      metrics =
          healthMetricRepository.findByUserAuthIdAndMetricTypeOrderByRecordedAtDesc(
              userId, filter.metricType());
    } else if (filter.startDate() != null && filter.endDate() != null) {
      metrics =
          healthMetricRepository.findByUserIdAndRecordedAtBetween(
              userId, filter.startDate(), filter.endDate());
    } else {
      metrics = healthMetricRepository.findByUserAuthIdOrderByRecordedAtDesc(userId);
    }

    return metrics.stream().map(healthMetricMapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public HealthMetricResponseDTO getHealthMetricById(UUID id, UUID userId) {
    log.debug("Fetching health metric: {} for user: {}", id, userId);

    HealthMetricEntity metric =
        healthMetricRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Health metric", "id", id));

    if (!metric.getUser().getAuthId().equals(userId)) {
      throw new ForbiddenException("You don't have permission to access this health metric");
    }

    return healthMetricMapper.toDto(metric);
  }

  @Override
  @Transactional(readOnly = true)
  public HealthMetricResponseDTO getLatestMetricByType(UUID userId, HealthMetricType metricType) {
    log.debug("Fetching latest {} metric for user: {}", metricType, userId);

    HealthMetricEntity metric =
        healthMetricRepository
            .findLatestByUserIdAndMetricType(userId, metricType)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Health metric", "type", metricType + " for user " + userId));

    return healthMetricMapper.toDto(metric);
  }

  @Override
  @Transactional
  public HealthMetricResponseDTO createHealthMetric(
      UUID userId, CreateHealthMetricRequestDTO request) {
    log.info("Creating health metric for user: {} of type: {}", userId, request.metricType());

    UserEntity user =
        userRepository
            .findByAuthId(userId)
            .orElseThrow(() -> new NotFoundException("User", "id", userId));

    HealthMetricEntity metric = healthMetricMapper.toEntity(request, user);
    metric = healthMetricRepository.save(metric);

    log.info("Successfully created health metric: {}", metric.getId());
    return healthMetricMapper.toDto(metric);
  }

  @Override
  @Transactional
  public List<HealthMetricResponseDTO> createHealthMetricsBatch(
      UUID userId, BatchCreateHealthMetricsRequestDTO request) {
    log.info(
        "Creating batch of {} health metrics for user: {}", request.metrics().size(), userId);

    UserEntity user =
        userRepository
            .findByAuthId(userId)
            .orElseThrow(() -> new NotFoundException("User", "id", userId));

    List<HealthMetricEntity> metrics =
        request.metrics().stream()
            .map(dto -> healthMetricMapper.toEntity(dto, user))
            .toList();

    metrics = healthMetricRepository.saveAll(metrics);

    log.info("Successfully created {} health metrics", metrics.size());
    return metrics.stream().map(healthMetricMapper::toDto).toList();
  }

  @Override
  @Transactional
  public HealthMetricResponseDTO updateHealthMetric(
      UUID id, UUID userId, UpdateHealthMetricRequestDTO request) {
    log.info("Updating health metric: {} for user: {}", id, userId);

    HealthMetricEntity metric =
        healthMetricRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Health metric", "id", id));

    if (!metric.getUser().getAuthId().equals(userId)) {
      throw new ForbiddenException("You don't have permission to update this health metric");
    }

    healthMetricMapper.updateEntity(metric, request);
    metric = healthMetricRepository.save(metric);

    log.info("Successfully updated health metric: {}", id);
    return healthMetricMapper.toDto(metric);
  }

  @Override
  @Transactional
  public void deleteHealthMetric(UUID id, UUID userId) {
    log.info("Deleting health metric: {} for user: {}", id, userId);

    HealthMetricEntity metric =
        healthMetricRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Health metric", "id", id));

    if (!metric.getUser().getAuthId().equals(userId)) {
      throw new ForbiddenException("You don't have permission to delete this health metric");
    }

    healthMetricRepository.delete(metric);
    log.info("Successfully deleted health metric: {}", id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<HealthMetricResponseDTO> getRecentHealthMetrics(UUID userId) {
    log.debug("Fetching health metrics for last 90 days for user: {}", userId);

    Instant ninetyDaysAgo = Instant.now().minus(90, ChronoUnit.DAYS);
    return healthMetricRepository.findByUserIdAndRecordedAtAfter(userId, ninetyDaysAgo).stream()
        .map(healthMetricMapper::toDto)
        .toList();
  }
}
