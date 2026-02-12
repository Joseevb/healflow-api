package dev.jose.healflow_api.api.controllers;

import dev.jose.healflow_api.api.docs.HealthMetricsApi;
import dev.jose.healflow_api.api.models.*;
import dev.jose.healflow_api.enumerations.HealthMetricType;
import dev.jose.healflow_api.services.HealthMetricService;
import dev.jose.healflow_api.services.HealthScoreService;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class HealthMetricsController implements HealthMetricsApi {

  private final HealthMetricService healthMetricService;
  private final HealthScoreService healthScoreService;

  @Override
  public ResponseEntity<List<HealthMetricResponseDTO>> getUserHealthMetrics(UUID userId) {
    return ResponseEntity.ok(healthMetricService.getUserHealthMetrics(userId));
  }

  @Override
  public ResponseEntity<List<HealthMetricResponseDTO>> getFilteredHealthMetrics(
      UUID userId, HealthMetricType metricType, Instant startDate, Instant endDate) {
    HealthMetricFilterRequestDTO filter =
        HealthMetricFilterRequestDTO.builder()
            .metricType(metricType)
            .startDate(startDate)
            .endDate(endDate)
            .build();
    return ResponseEntity.ok(healthMetricService.getFilteredHealthMetrics(userId, filter));
  }

  @Override
  public ResponseEntity<HealthMetricResponseDTO> getHealthMetricById(UUID id, UUID userId) {
    return ResponseEntity.ok(healthMetricService.getHealthMetricById(id, userId));
  }

  @Override
  public ResponseEntity<HealthMetricResponseDTO> getLatestMetricByType(
      HealthMetricType metricType, UUID userId) {
    return ResponseEntity.ok(healthMetricService.getLatestMetricByType(userId, metricType));
  }

  @Override
  public ResponseEntity<HealthMetricResponseDTO> createHealthMetric(
      UUID userId, CreateHealthMetricRequestDTO request, UriComponentsBuilder uriBuilder) {
    var res = healthMetricService.createHealthMetric(userId, request);
    String location = uriBuilder.path("/{id}").buildAndExpand(res.id()).toUriString();

    // Asynchronously recalculate health score
    healthScoreService.recalculateHealthScoreAsync(userId);

    return ResponseEntity.created(URI.create(location)).body(res);
  }

  @Override
  public ResponseEntity<List<HealthMetricResponseDTO>> createHealthMetricsBatch(
      UUID userId, BatchCreateHealthMetricsRequestDTO request) {
    var res = healthMetricService.createHealthMetricsBatch(userId, request);

    // Asynchronously recalculate health score
    healthScoreService.recalculateHealthScoreAsync(userId);

    return ResponseEntity.status(201).body(res);
  }

  @Override
  public ResponseEntity<HealthMetricResponseDTO> updateHealthMetric(
      UUID id, UUID userId, UpdateHealthMetricRequestDTO request) {
    return ResponseEntity.ok(healthMetricService.updateHealthMetric(id, userId, request));
  }

  @Override
  public ResponseEntity<Void> deleteHealthMetric(UUID id, UUID userId) {
    healthMetricService.deleteHealthMetric(id, userId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<List<HealthMetricResponseDTO>> getRecentHealthMetrics(UUID userId) {
    return ResponseEntity.ok(healthMetricService.getRecentHealthMetrics(userId));
  }

  @Override
  public ResponseEntity<HealthScoreResponseDTO> getLatestHealthScore(UUID userId) {
    return ResponseEntity.ok(healthScoreService.getLatestHealthScore(userId));
  }

  @Override
  public ResponseEntity<List<HealthScoreResponseDTO>> getHealthScoreHistory(UUID userId) {
    return ResponseEntity.ok(healthScoreService.getHealthScoreHistory(userId));
  }

  @Override
  public ResponseEntity<HealthScoreResponseDTO> calculateHealthScore(
      UUID userId, UriComponentsBuilder uriBuilder) {
    var res = healthScoreService.calculateHealthScore(userId);
    String location =
        uriBuilder.path("/score/history").buildAndExpand(res.id()).toUriString();

    return ResponseEntity.created(URI.create(location)).body(res);
  }
}
