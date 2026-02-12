package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.HealthMetricSummaryDTO;
import dev.jose.healflow_api.api.models.HealthScoreResponseDTO;
import dev.jose.healflow_api.api.models.RecommendationDTO;
import dev.jose.healflow_api.enumerations.HealthMetricType;
import dev.jose.healflow_api.exceptions.NotFoundException;
import dev.jose.healflow_api.mappers.HealthScoreMapper;
import dev.jose.healflow_api.persistence.entities.HealthMetricEntity;
import dev.jose.healflow_api.persistence.entities.HealthScoreEntity;
import dev.jose.healflow_api.persistence.entities.UserEntity;
import dev.jose.healflow_api.persistence.repositories.HealthMetricRepository;
import dev.jose.healflow_api.persistence.repositories.HealthScoreRepository;
import dev.jose.healflow_api.persistence.repositories.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthScoreServiceImpl implements HealthScoreService {

  private static final int PERIOD_DAYS = 90;
  private static final int EXCELLENT_THRESHOLD = 90;
  private static final int FAIR_THRESHOLD = 60;
  private static final int NEEDS_IMPROVEMENT_THRESHOLD = 40;

  private final HealthScoreRepository healthScoreRepository;
  private final HealthMetricRepository healthMetricRepository;
  private final UserRepository userRepository;
  private final HealthScoreMapper healthScoreMapper;

  @Override
  @Transactional(readOnly = true)
  public HealthScoreResponseDTO getLatestHealthScore(UUID userId) {
    log.debug("Fetching latest health score for user: {}", userId);

    HealthScoreEntity scoreEntity =
        healthScoreRepository
            .findLatestByUserId(userId)
            .orElseThrow(() -> new NotFoundException("HealthScore", "userId", userId));

    return buildHealthScoreResponse(scoreEntity, userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<HealthScoreResponseDTO> getHealthScoreHistory(UUID userId) {
    log.debug("Fetching health score history for user: {}", userId);

    List<HealthScoreEntity> scores =
        healthScoreRepository.findTop10ByUserAuthIdOrderByCalculatedAtDesc(userId);

    return scores.stream().map(score -> buildHealthScoreResponse(score, userId)).toList();
  }

  @Override
  @Transactional
  public HealthScoreResponseDTO calculateHealthScore(UUID userId) {
    log.info("Calculating health score for user: {}", userId);

    UserEntity user =
        userRepository
            .findByAuthId(userId)
            .orElseThrow(() -> new NotFoundException("User", "id", userId));

    Instant periodStart = Instant.now().minus(PERIOD_DAYS, ChronoUnit.DAYS);
    List<HealthMetricEntity> recentMetrics =
        healthMetricRepository.findByUserIdAndRecordedAtAfter(userId, periodStart);

    if (recentMetrics.isEmpty()) {
      log.warn("No health metrics found for user: {} in the last {} days", userId, PERIOD_DAYS);
      throw new NotFoundException(
          "Health metrics", "userId", userId + " (no data in the last " + PERIOD_DAYS + " days)");
    }

    // Group metrics by type
    Map<HealthMetricType, List<HealthMetricEntity>> metricsByType =
        recentMetrics.stream().collect(Collectors.groupingBy(HealthMetricEntity::getMetricType));

    // Calculate subscores
    Integer cardiovascularScore = calculateCardiovascularScore(metricsByType);
    Integer metabolicScore = calculateMetabolicScore(metricsByType);
    Integer lifestyleScore = calculateLifestyleScore(metricsByType);
    Integer vitalSignsScore = calculateVitalSignsScore(metricsByType);

    // Calculate weighted overall score
    int overallScore =
        calculateOverallScore(cardiovascularScore, metabolicScore, lifestyleScore, vitalSignsScore);

    // Save the health score
    HealthScoreEntity scoreEntity =
        HealthScoreEntity.builder()
            .user(user)
            .overallScore(overallScore)
            .cardiovascularScore(cardiovascularScore)
            .metabolicScore(metabolicScore)
            .lifestyleScore(lifestyleScore)
            .vitalSignsScore(vitalSignsScore)
            .calculatedAt(Instant.now())
            .dataPointsCount(recentMetrics.size())
            .periodDays(PERIOD_DAYS)
            .build();

    scoreEntity = healthScoreRepository.save(scoreEntity);

    log.info("Successfully calculated health score: {} for user: {}", overallScore, userId);
    return buildHealthScoreResponse(scoreEntity, userId);
  }

  @Async
  @Override
  public void recalculateHealthScoreAsync(UUID userId) {
    try {
      log.info("Asynchronously recalculating health score for user: {}", userId);
      calculateHealthScore(userId);
    } catch (Exception e) {
      log.error("Failed to recalculate health score for user: {}", userId, e);
    }
  }

  private HealthScoreResponseDTO buildHealthScoreResponse(
      HealthScoreEntity scoreEntity, UUID userId) {
    HealthScoreResponseDTO baseDto = healthScoreMapper.toDto(scoreEntity);

    // Get recent metrics summary
    List<HealthMetricSummaryDTO> recentMetrics = getRecentMetricsSummary(userId);

    // Generate recommendations
    List<RecommendationDTO> recommendations =
        generateRecommendations(scoreEntity, getLatestMetricsByType(userId));

    return HealthScoreResponseDTO.builder()
        .id(baseDto.id())
        .overallScore(baseDto.overallScore())
        .cardiovascularScore(baseDto.cardiovascularScore())
        .metabolicScore(baseDto.metabolicScore())
        .lifestyleScore(baseDto.lifestyleScore())
        .vitalSignsScore(baseDto.vitalSignsScore())
        .calculatedAt(baseDto.calculatedAt())
        .dataPointsCount(baseDto.dataPointsCount())
        .periodDays(baseDto.periodDays())
        .recentMetrics(recentMetrics)
        .recommendations(recommendations)
        .build();
  }

  private List<HealthMetricSummaryDTO> getRecentMetricsSummary(UUID userId) {
    Instant periodStart = Instant.now().minus(PERIOD_DAYS, ChronoUnit.DAYS);
    List<HealthMetricEntity> recentMetrics =
        healthMetricRepository.findByUserIdAndRecordedAtAfter(userId, periodStart);

    Map<HealthMetricType, HealthMetricEntity> latestByType = new HashMap<>();
    for (HealthMetricEntity metric : recentMetrics) {
      latestByType.merge(
          metric.getMetricType(),
          metric,
          (existing, newMetric) ->
              newMetric.getRecordedAt().isAfter(existing.getRecordedAt()) ? newMetric : existing);
    }

    return latestByType.values().stream()
        .map(
            metric -> {
              String trend = calculateTrend(userId, metric.getMetricType(), metric);
              return HealthMetricSummaryDTO.builder()
                  .metricType(metric.getMetricType())
                  .latestValue(metric.getValue())
                  .unit(metric.getUnit())
                  .recordedAt(metric.getRecordedAt())
                  .trend(trend)
                  .build();
            })
        .toList();
  }

  private String calculateTrend(
      UUID userId, HealthMetricType metricType, HealthMetricEntity latest) {

    var twoWeeksAgo = latest.getRecordedAt().minus(14, ChronoUnit.DAYS);
    var fourWeeksAgo = latest.getRecordedAt().minus(28, ChronoUnit.DAYS);

    var historicalMetrics =
        healthMetricRepository.findByUserIdAndMetricTypeAndDateRange(
            userId, metricType, fourWeeksAgo, latest.getRecordedAt());

    if (historicalMetrics.size() < 3) {
      return "insufficient_data";
    }

    var recentMetrics =
        historicalMetrics.stream().filter(m -> m.getRecordedAt().isAfter(twoWeeksAgo)).toList();

    var olderMetrics =
        historicalMetrics.stream().filter(m -> m.getRecordedAt().isBefore(twoWeeksAgo)).toList();

    var recentAvg = calculateAverage(recentMetrics);
    var olderAvg = calculateAverage(olderMetrics);

    var changePercent =
        recentAvg
            .subtract(olderAvg)
            .divide(olderAvg, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));

    if (changePercent.abs().compareTo(BigDecimal.valueOf(5)) < 0) {
      return "stable";
    }

    var lowerIsBetter =
        Set.of(
                HealthMetricType.BLOOD_PRESSURE_SYSTOLIC,
                HealthMetricType.BLOOD_PRESSURE_DIASTOLIC,
                HealthMetricType.BLOOD_GLUCOSE,
                HealthMetricType.CHOLESTEROL_LDL,
                HealthMetricType.TRIGLYCERIDES)
            .contains(metricType);

    var isIncreasing = changePercent.compareTo(BigDecimal.ZERO) > 0;
    return (lowerIsBetter ? !isIncreasing : isIncreasing) ? "improving" : "declining";
  }

  private BigDecimal calculateAverage(List<HealthMetricEntity> metrics) {
    return metrics.stream()
        .map(HealthMetricEntity::getValue)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(BigDecimal.valueOf(metrics.size()), 2, RoundingMode.HALF_UP);
  }

  private Map<HealthMetricType, HealthMetricEntity> getLatestMetricsByType(UUID userId) {
    Instant periodStart = Instant.now().minus(PERIOD_DAYS, ChronoUnit.DAYS);
    List<HealthMetricEntity> recentMetrics =
        healthMetricRepository.findByUserIdAndRecordedAtAfter(userId, periodStart);

    Map<HealthMetricType, HealthMetricEntity> latestByType = new HashMap<>();
    for (HealthMetricEntity metric : recentMetrics) {
      latestByType.merge(
          metric.getMetricType(),
          metric,
          (existing, newMetric) ->
              newMetric.getRecordedAt().isAfter(existing.getRecordedAt()) ? newMetric : existing);
    }
    return latestByType;
  }

  /**
   * Calculate cardiovascular health score (0-100) Based on: blood pressure, heart rate, oxygen
   * saturation
   */
  private Integer calculateCardiovascularScore(
      Map<HealthMetricType, List<HealthMetricEntity>> metricsByType) {

    return calculateAverageScore(
        scoreMetric(
            metricsByType,
            HealthMetricType.BLOOD_PRESSURE_SYSTOLIC,
            this::scoreBloodPressureSystolic),
        scoreMetric(
            metricsByType,
            HealthMetricType.BLOOD_PRESSURE_DIASTOLIC,
            this::scoreBloodPressureDiastolic),
        scoreMetric(metricsByType, HealthMetricType.HEART_RATE, this::scoreHeartRate),
        scoreMetric(
            metricsByType, HealthMetricType.OXYGEN_SATURATION, this::scoreOxygenSaturation));
  }

  /**
   * Calculate metabolic health score (0-100) Based on: BMI, blood glucose, HbA1c, cholesterol
   * levels
   */
  private Integer calculateMetabolicScore(
      Map<HealthMetricType, List<HealthMetricEntity>> metricsByType) {

    return calculateAverageScore(
        scoreMetric(metricsByType, HealthMetricType.BMI, this::scoreBMI),
        scoreMetric(metricsByType, HealthMetricType.BLOOD_GLUCOSE, this::scoreBloodGlucose),
        scoreMetric(metricsByType, HealthMetricType.HBA1C, this::scoreHbA1c),
        scoreMetric(metricsByType, HealthMetricType.CHOLESTEROL_TOTAL, this::scoreTotalCholesterol),
        scoreMetric(metricsByType, HealthMetricType.CHOLESTEROL_LDL, this::scoreLDLCholesterol),
        scoreMetric(metricsByType, HealthMetricType.CHOLESTEROL_HDL, this::scoreHDLCholesterol),
        scoreMetric(metricsByType, HealthMetricType.TRIGLYCERIDES, this::scoreTriglycerides));
  }

  /** Calculate lifestyle health score (0-100) Based on: sleep, exercise, water intake, steps */
  private Integer calculateLifestyleScore(
      Map<HealthMetricType, List<HealthMetricEntity>> metricsByType) {

    return calculateAverageScore(
        scoreMetric(metricsByType, HealthMetricType.SLEEP_HOURS, this::scoreSleepHours),
        scoreMetric(metricsByType, HealthMetricType.EXERCISE_MINUTES, this::scoreExerciseMinutes),
        scoreMetric(metricsByType, HealthMetricType.WATER_INTAKE, this::scoreWaterIntake),
        scoreMetric(metricsByType, HealthMetricType.STEPS, this::scoreSteps));
  }

  @SafeVarargs
  private final Integer calculateAverageScore(Optional<Integer>... optionalScores) {
    var scores = Stream.of(optionalScores).flatMap(Optional::stream).toList();

    return scores.isEmpty()
        ? null
        : (int) scores.stream().mapToInt(Integer::intValue).average().orElse(0);
  }

  /** Calculate vital signs score (0-100) Based on: body temperature, respiratory rate */
  private Integer calculateVitalSignsScore(
      Map<HealthMetricType, List<HealthMetricEntity>> metricsByType) {

    var scores =
        Stream.of(
                scoreMetric(
                    metricsByType, HealthMetricType.BODY_TEMPERATURE, this::scoreBodyTemperature),
                scoreMetric(
                    metricsByType, HealthMetricType.RESPIRATORY_RATE, this::scoreRespiratoryRate))
            .flatMap(Optional::stream)
            .toList();

    return scores.isEmpty()
        ? null
        : (int) scores.stream().mapToInt(Integer::intValue).average().orElse(0);
  }

  private Optional<Integer> scoreMetric(
      Map<HealthMetricType, List<HealthMetricEntity>> metricsByType,
      HealthMetricType type,
      Function<BigDecimal, Integer> scoringFunction) {

    return Optional.ofNullable(metricsByType.get(type))
        .map(this::getAverageValue)
        .map(scoringFunction);
  }

  private int calculateOverallScore(
      Integer cardiovascularScore,
      Integer metabolicScore,
      Integer lifestyleScore,
      Integer vitalSignsScore) {

    var weightedScores =
        Stream.of(
                Optional.ofNullable(cardiovascularScore).map(s -> new WeightedScore(s, 0.35)),
                Optional.ofNullable(metabolicScore).map(s -> new WeightedScore(s, 0.35)),
                Optional.ofNullable(lifestyleScore).map(s -> new WeightedScore(s, 0.20)),
                Optional.ofNullable(vitalSignsScore).map(s -> new WeightedScore(s, 0.10)))
            .flatMap(Optional::stream)
            .toList();

    if (weightedScores.isEmpty()) return 0;

    double totalWeight = weightedScores.stream().mapToDouble(ws -> ws.weight).sum();
    double weightedSum =
        weightedScores.stream().mapToDouble(ws -> ws.score * (ws.weight / totalWeight)).sum();

    return (int) Math.round(weightedSum);
  }

  private record WeightedScore(int score, double weight) {}

  // Scoring helper methods
  private BigDecimal getAverageValue(List<HealthMetricEntity> metrics) {
    return metrics.stream()
        .map(HealthMetricEntity::getValue)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(BigDecimal.valueOf(metrics.size()), 2, RoundingMode.HALF_UP);
  }

  // Individual metric scoring methods based on medical standards

  private int scoreBloodPressureSystolic(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(90)) < 0 -> 40;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(120)) <= 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(129)) <= 0 -> 85;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(139)) <= 0 -> 65;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(159)) <= 0 -> 45;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(179)) <= 0 -> 25;
      default -> 10;
    };
  }

  private int scoreBloodPressureDiastolic(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(60)) < 0 -> 40;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(80)) <= 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(89)) <= 0 -> 70;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(99)) <= 0 -> 45;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(119)) <= 0 -> 25;
      default -> 10;
    };
  }

  private int scoreHeartRate(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(40)) < 0 -> 30;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(60)) <= 0 -> 95;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(100)) <= 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(110)) <= 0 -> 75;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(120)) <= 0 -> 50;
      default -> 25;
    };
  }

  private int scoreOxygenSaturation(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(95)) >= 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(90)) >= 0 -> 75;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(85)) >= 0 -> 40;
      default -> 15;
    };
  }

  private int scoreBMI(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(18.5)) < 0 -> 60;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(24.9)) <= 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(29.9)) <= 0 -> 70;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(34.9)) <= 0 -> 45;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(39.9)) <= 0 -> 25;
      default -> 10;
    };
  }

  private int scoreBloodGlucose(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(70)) < 0 -> 50;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(100)) <= 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(125)) <= 0 -> 60;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(150)) <= 0 -> 35;
      default -> 15;
    };
  }

  private int scoreHbA1c(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(5.7)) < 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(6.4)) <= 0 -> 65;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(7.0)) <= 0 -> 45;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(8.0)) <= 0 -> 30;
      default -> 15;
    };
  }

  private int scoreTotalCholesterol(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(200)) < 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(239)) <= 0 -> 70;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(279)) <= 0 -> 45;
      default -> 25;
    };
  }

  private int scoreLDLCholesterol(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(100)) < 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(129)) <= 0 -> 85;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(159)) <= 0 -> 65;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(189)) <= 0 -> 40;
      default -> 20;
    };
  }

  private int scoreHDLCholesterol(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(60)) >= 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(50)) >= 0 -> 80;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(40)) >= 0 -> 60;
      default -> 35;
    };
  }

  private int scoreTriglycerides(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(150)) < 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(199)) <= 0 -> 75;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(499)) <= 0 -> 45;
      default -> 20;
    };
  }

  private int scoreSleepHours(BigDecimal value) {
    return switch (value) {
      case BigDecimal v
          when v.compareTo(BigDecimal.valueOf(7)) >= 0 && v.compareTo(BigDecimal.valueOf(9)) <= 0 ->
          100;
      case BigDecimal v
          when v.compareTo(BigDecimal.valueOf(6)) >= 0
              && v.compareTo(BigDecimal.valueOf(10)) <= 0 ->
          80;
      case BigDecimal v
          when v.compareTo(BigDecimal.valueOf(5)) >= 0
              && v.compareTo(BigDecimal.valueOf(11)) <= 0 ->
          55;
      default -> 30;
    };
  }

  private int scoreExerciseMinutes(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(30)) >= 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(22)) >= 0 -> 85;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(15)) >= 0 -> 65;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(10)) >= 0 -> 45;
      default -> 25;
    };
  }

  private int scoreWaterIntake(BigDecimal value) {
    return switch (value) {
      case BigDecimal v
          when v.compareTo(BigDecimal.valueOf(2)) >= 0
              && v.compareTo(BigDecimal.valueOf(3.5)) <= 0 ->
          100;
      case BigDecimal v
          when v.compareTo(BigDecimal.valueOf(1.5)) >= 0
              && v.compareTo(BigDecimal.valueOf(4)) <= 0 ->
          80;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(1)) >= 0 -> 55;
      default -> 30;
    };
  }

  private int scoreSteps(BigDecimal value) {
    return switch (value) {
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(10000)) >= 0 -> 100;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(7500)) >= 0 -> 85;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(5000)) >= 0 -> 65;
      case BigDecimal v when v.compareTo(BigDecimal.valueOf(2500)) >= 0 -> 45;
      default -> 25;
    };
  }

  private int scoreBodyTemperature(BigDecimal value) {
    return switch (value) {
      case BigDecimal v
          when v.compareTo(BigDecimal.valueOf(36.5)) >= 0
              && v.compareTo(BigDecimal.valueOf(37.5)) <= 0 ->
          100;
      case BigDecimal v
          when v.compareTo(BigDecimal.valueOf(36)) >= 0
              && v.compareTo(BigDecimal.valueOf(38)) <= 0 ->
          75;
      case BigDecimal v
          when v.compareTo(BigDecimal.valueOf(35.5)) >= 0
              && v.compareTo(BigDecimal.valueOf(38.5)) <= 0 ->
          45;
      default -> 20;
    };
  }

  private int scoreRespiratoryRate(BigDecimal value) {
    return switch (value) {
      case BigDecimal v
          when v.compareTo(BigDecimal.valueOf(12)) >= 0
              && v.compareTo(BigDecimal.valueOf(20)) <= 0 ->
          100;
      case BigDecimal v
          when v.compareTo(BigDecimal.valueOf(10)) >= 0
              && v.compareTo(BigDecimal.valueOf(24)) <= 0 ->
          70;
      case BigDecimal v
          when v.compareTo(BigDecimal.valueOf(8)) >= 0
              && v.compareTo(BigDecimal.valueOf(28)) <= 0 ->
          40;
      default -> 20;
    };
  }

  private List<RecommendationDTO> generateRecommendations(
      HealthScoreEntity scoreEntity, Map<HealthMetricType, HealthMetricEntity> latestMetrics) {

    List<RecommendationDTO> recommendations = new ArrayList<>();

    // Cardiovascular recommendations
    if (scoreEntity.getCardiovascularScore() != null) {
      recommendations.addAll(
          generateCardiovascularRecommendations(
              scoreEntity.getCardiovascularScore(), latestMetrics));
    }

    // Metabolic recommendations
    if (scoreEntity.getMetabolicScore() != null) {
      recommendations.addAll(
          generateMetabolicRecommendations(scoreEntity.getMetabolicScore(), latestMetrics));
    }

    // Lifestyle recommendations
    if (scoreEntity.getLifestyleScore() != null) {
      recommendations.addAll(
          generateLifestyleRecommendations(scoreEntity.getLifestyleScore(), latestMetrics));
    }

    // Overall score recommendations
    if (scoreEntity.getOverallScore() >= EXCELLENT_THRESHOLD) {
      recommendations.add(
          RecommendationDTO.builder()
              .category("overall")
              .message("Excellent health! Keep up your healthy habits.")
              .priority("low")
              .build());
    } else if (scoreEntity.getOverallScore() < NEEDS_IMPROVEMENT_THRESHOLD) {
      recommendations.add(
          RecommendationDTO.builder()
              .category("overall")
              .message(
                  "Your health score indicates areas needing attention. Please consult with your"
                      + " healthcare provider.")
              .priority("high")
              .build());
    }

    return recommendations;
  }

  private List<RecommendationDTO> generateCardiovascularRecommendations(
      int score, Map<HealthMetricType, HealthMetricEntity> latestMetrics) {

    List<RecommendationDTO> recommendations = new ArrayList<>();

    if (latestMetrics.containsKey(HealthMetricType.BLOOD_PRESSURE_SYSTOLIC)) {
      BigDecimal systolic = latestMetrics.get(HealthMetricType.BLOOD_PRESSURE_SYSTOLIC).getValue();
      if (systolic.compareTo(BigDecimal.valueOf(130)) > 0) {
        recommendations.add(
            RecommendationDTO.builder()
                .category("cardiovascular")
                .message(
                    "Your blood pressure is elevated. Consider reducing sodium intake and"
                        + " increasing physical activity.")
                .priority(systolic.compareTo(BigDecimal.valueOf(140)) > 0 ? "high" : "medium")
                .build());
      }
    }

    if (latestMetrics.containsKey(HealthMetricType.HEART_RATE)) {
      BigDecimal heartRate = latestMetrics.get(HealthMetricType.HEART_RATE).getValue();
      if (heartRate.compareTo(BigDecimal.valueOf(100)) > 0) {
        recommendations.add(
            RecommendationDTO.builder()
                .category("cardiovascular")
                .message(
                    "Your resting heart rate is elevated. Regular aerobic exercise can help lower"
                        + " it.")
                .priority("medium")
                .build());
      }
    }

    if (score < FAIR_THRESHOLD) {
      recommendations.add(
          RecommendationDTO.builder()
              .category("cardiovascular")
              .message(
                  "Your cardiovascular health needs attention. Schedule a checkup with your"
                      + " doctor.")
              .priority("high")
              .build());
    }

    return recommendations;
  }

  private List<RecommendationDTO> generateMetabolicRecommendations(
      int score, Map<HealthMetricType, HealthMetricEntity> latestMetrics) {

    List<RecommendationDTO> recommendations = new ArrayList<>();

    if (latestMetrics.containsKey(HealthMetricType.BMI)) {
      BigDecimal bmi = latestMetrics.get(HealthMetricType.BMI).getValue();
      if (bmi.compareTo(BigDecimal.valueOf(25)) > 0) {
        recommendations.add(
            RecommendationDTO.builder()
                .category("metabolic")
                .message(
                    "Your BMI indicates you're overweight. Consider a balanced diet and regular"
                        + " exercise.")
                .priority(bmi.compareTo(BigDecimal.valueOf(30)) > 0 ? "high" : "medium")
                .build());
      } else if (bmi.compareTo(BigDecimal.valueOf(18.5)) < 0) {
        recommendations.add(
            RecommendationDTO.builder()
                .category("metabolic")
                .message("Your BMI is below normal. Consult a nutritionist for guidance.")
                .priority("medium")
                .build());
      }
    }

    if (latestMetrics.containsKey(HealthMetricType.BLOOD_GLUCOSE)) {
      BigDecimal glucose = latestMetrics.get(HealthMetricType.BLOOD_GLUCOSE).getValue();
      if (glucose.compareTo(BigDecimal.valueOf(100)) > 0) {
        recommendations.add(
            RecommendationDTO.builder()
                .category("metabolic")
                .message(
                    "Your blood glucose is elevated. Limit sugar intake and maintain a healthy"
                        + " weight.")
                .priority(glucose.compareTo(BigDecimal.valueOf(126)) >= 0 ? "high" : "medium")
                .build());
      }
    }

    if (latestMetrics.containsKey(HealthMetricType.CHOLESTEROL_LDL)) {
      BigDecimal ldl = latestMetrics.get(HealthMetricType.CHOLESTEROL_LDL).getValue();
      if (ldl.compareTo(BigDecimal.valueOf(130)) > 0) {
        recommendations.add(
            RecommendationDTO.builder()
                .category("metabolic")
                .message(
                    "Your LDL cholesterol is high. Reduce saturated fat intake and increase fiber.")
                .priority(ldl.compareTo(BigDecimal.valueOf(160)) > 0 ? "high" : "medium")
                .build());
      }
    }

    return recommendations;
  }

  private List<RecommendationDTO> generateLifestyleRecommendations(
      int score, Map<HealthMetricType, HealthMetricEntity> latestMetrics) {

    List<RecommendationDTO> recommendations = new ArrayList<>();

    if (latestMetrics.containsKey(HealthMetricType.SLEEP_HOURS)) {
      BigDecimal sleep = latestMetrics.get(HealthMetricType.SLEEP_HOURS).getValue();
      if (sleep.compareTo(BigDecimal.valueOf(7)) < 0) {
        recommendations.add(
            RecommendationDTO.builder()
                .category("lifestyle")
                .message("You're not getting enough sleep. Aim for 7-9 hours per night.")
                .priority("medium")
                .build());
      }
    }

    if (latestMetrics.containsKey(HealthMetricType.EXERCISE_MINUTES)) {
      BigDecimal exercise = latestMetrics.get(HealthMetricType.EXERCISE_MINUTES).getValue();
      if (exercise.compareTo(BigDecimal.valueOf(22)) < 0) {
        recommendations.add(
            RecommendationDTO.builder()
                .category("lifestyle")
                .message(
                    "Increase your daily physical activity. Aim for at least 150 minutes per week.")
                .priority("medium")
                .build());
      }
    }

    if (latestMetrics.containsKey(HealthMetricType.WATER_INTAKE)) {
      BigDecimal water = latestMetrics.get(HealthMetricType.WATER_INTAKE).getValue();
      if (water.compareTo(BigDecimal.valueOf(2)) < 0) {
        recommendations.add(
            RecommendationDTO.builder()
                .category("lifestyle")
                .message("Increase your water intake. Aim for 2-3 liters daily.")
                .priority("low")
                .build());
      }
    }

    if (latestMetrics.containsKey(HealthMetricType.STEPS)) {
      BigDecimal steps = latestMetrics.get(HealthMetricType.STEPS).getValue();
      if (steps.compareTo(BigDecimal.valueOf(5000)) < 0) {
        recommendations.add(
            RecommendationDTO.builder()
                .category("lifestyle")
                .message("Try to walk more. Aim for at least 10,000 steps per day.")
                .priority("medium")
                .build());
      }
    }

    return recommendations;
  }
}
