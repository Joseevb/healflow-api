package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.AddMedicineToUserRequestDTO;
import dev.jose.healflow_api.api.models.ProvisionUserRequestDTO;
import dev.jose.healflow_api.api.models.ValidateAuthUserIdsDTO;
import dev.jose.healflow_api.enumerations.HealthMetricType;
import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import dev.jose.healflow_api.exceptions.AuthUserIdValidationException;
import dev.jose.healflow_api.persistence.entities.HealthMetricEntity;
import dev.jose.healflow_api.persistence.entities.SpecialistEntity;
import dev.jose.healflow_api.persistence.entities.UserEntity;
import dev.jose.healflow_api.persistence.repositories.HealthMetricRepository;
import dev.jose.healflow_api.persistence.repositories.SpecialistAvailabilityRepository;
import dev.jose.healflow_api.persistence.repositories.SpecialistRepository;
import dev.jose.healflow_api.persistence.repositories.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProvisionServiceImpl implements UserProvisionService {

  private final UserRepository userRepository;
  private final SpecialistRepository specialistRepository;
  private final SpecialistAvailabilityRepository availabilityRepository;
  private final HealthMetricRepository healthMetricRepository;

  // TODO: Remove
  private final MedicineService medicineService;
  private final UserMedicineService userMedicineService;

  private final Random random = new Random();

  @Override
  @Transactional
  public String provisionUser(ProvisionUserRequestDTO request) {
    if (userRepository.existsByEmail(request.email())
        || userRepository.existsByAuthId(request.userId())) {
      throw new IllegalArgumentException("User already exists");
    }

    // Determine primary specialist
    SpecialistEntity primarySpecialist;
    if (request.specialistId() != null) {
      primarySpecialist =
          specialistRepository
              .findById(request.specialistId())
              .orElseThrow(() -> new IllegalArgumentException("Specialist not found"));
    } else {
      // Find default specialist (General Practice with most availability)
      primarySpecialist = findDefaultSpecialist();
    }

    // Parse names with defaults
    String firstName = request.firstName() != null ? request.firstName() : "User";
    String lastName = request.lastName() != null ? request.lastName() : "";
    String phone = request.phone() != null ? request.phone() : "";

    var entity =
        UserEntity.builder()
            .email(request.email())
            .authId(request.userId())
            .firstName(firstName)
            .lastName(lastName)
            .phone(phone)
            .primarySpecialist(primarySpecialist)
            .isSubscribed(Boolean.TRUE.equals(request.isSubscribed()))
            .build();

    // Persist first to obtain generated id
    var saved = userRepository.save(entity);

    log.info(
        "Provisioned user: {} {} ({}), assigned to specialist: {} {}",
        firstName,
        lastName,
        request.email(),
        primarySpecialist.getFirstName(),
        primarySpecialist.getLastName());

    // Now attach medicines to the persisted user
    addMedicineToUser(saved);

    // Generate test health metrics for the user
    generateTestHealthMetrics(saved);

    return saved.getId().toString();
  }

  /**
   * Find the default specialist for new users. Selects a General Practice specialist with the most
   * availability slots.
   *
   * @return SpecialistEntity with the most availability
   * @throws IllegalStateException if no General Practice specialist is found
   */
  private SpecialistEntity findDefaultSpecialist() {
    List<SpecialistEntity> generalPracticeSpecialists =
        specialistRepository.findActiveBySpecialistType(SpecialistTypeEnum.GENERAL_PRACTICE);

    if (generalPracticeSpecialists.isEmpty()) {
      throw new IllegalStateException("No active General Practice specialists found in the system");
    }

    // Find specialist with most availability days
    return generalPracticeSpecialists.stream()
        .max(
            (s1, s2) -> {
              long s1AvailabilityCount =
                  availabilityRepository.findBySpecialistAndIsAvailableTrue(s1).size();
              long s2AvailabilityCount =
                  availabilityRepository.findBySpecialistAndIsAvailableTrue(s2).size();
              return Long.compare(s1AvailabilityCount, s2AvailabilityCount);
            })
        .orElseThrow(() -> new IllegalStateException("Unable to determine default specialist"));
  }

  // TODO: Remove this method
  private void addMedicineToUser(UserEntity user) {
    medicineService
        .searchMedicines(null, "Human", 1, Map.of("nameOfMedicine", "Ibuprofen"))
        .getData()
        .forEach(
            m ->
                userMedicineService.addMedicineToUser(
                    user.getId(),
                    AddMedicineToUserRequestDTO.builder()
                        .userId(user.getId())
                        .medicineId(m.getId())
                        .dosage("1")
                        .frequency("daily")
                        .startDate(java.time.LocalDateTime.now())
                        .endDate(java.time.LocalDateTime.now().plusDays(7))
                        .build()));
  }

  @Override
  public void validateUserIds(ValidateAuthUserIdsDTO userIds) {
    var invalidIds =
        userIds.ids().stream().filter(id -> !userRepository.existsByAuthId(id)).toList();

    if (!invalidIds.isEmpty()) {
      throw new AuthUserIdValidationException(invalidIds);
    }
  }

  /**
   * Generate random test health metrics for a newly provisioned user. Creates metrics spanning the
   * last 90 days with realistic values.
   *
   * @param user The user entity to generate metrics for
   */
  private void generateTestHealthMetrics(UserEntity user) {
    log.info("Generating test health metrics for user: {}", user.getId());
    List<HealthMetricEntity> metrics = new ArrayList<>();
    Instant now = Instant.now();

    // Generate metrics for the past 90 days (3 entries per week for variety)
    for (int daysAgo = 0; daysAgo <= 90; daysAgo += 3) {
      Instant recordedAt = now.minus(daysAgo, ChronoUnit.DAYS);

      // Cardiovascular metrics
      metrics.add(
          createMetric(
              user, HealthMetricType.BLOOD_PRESSURE_SYSTOLIC, randomInRange(110, 140), recordedAt));
      metrics.add(
          createMetric(
              user, HealthMetricType.BLOOD_PRESSURE_DIASTOLIC, randomInRange(70, 90), recordedAt));
      metrics.add(
          createMetric(user, HealthMetricType.HEART_RATE, randomInRange(60, 100), recordedAt));
      metrics.add(
          createMetric(
              user, HealthMetricType.OXYGEN_SATURATION, randomInRange(95, 100), recordedAt));

      // Metabolic metrics
      metrics.add(createMetric(user, HealthMetricType.WEIGHT, randomInRange(60, 90), recordedAt));
      metrics.add(
          createMetric(user, HealthMetricType.BLOOD_GLUCOSE, randomInRange(70, 120), recordedAt));
      metrics.add(
          createMetric(
              user, HealthMetricType.CHOLESTEROL_TOTAL, randomInRange(150, 220), recordedAt));
      metrics.add(
          createMetric(user, HealthMetricType.CHOLESTEROL_LDL, randomInRange(70, 130), recordedAt));
      metrics.add(
          createMetric(user, HealthMetricType.CHOLESTEROL_HDL, randomInRange(40, 80), recordedAt));
      metrics.add(
          createMetric(user, HealthMetricType.TRIGLYCERIDES, randomInRange(50, 150), recordedAt));

      // Vital signs
      metrics.add(
          createMetric(
              user, HealthMetricType.BODY_TEMPERATURE, randomInRange(36.1, 37.2), recordedAt));
      metrics.add(
          createMetric(user, HealthMetricType.RESPIRATORY_RATE, randomInRange(12, 20), recordedAt));

      // Lifestyle metrics
      metrics.add(
          createMetric(user, HealthMetricType.SLEEP_HOURS, randomInRange(5, 9), recordedAt));
      metrics.add(
          createMetric(user, HealthMetricType.EXERCISE_MINUTES, randomInRange(0, 90), recordedAt));
      metrics.add(
          createMetric(user, HealthMetricType.WATER_INTAKE, randomInRange(1.0, 3.5), recordedAt));
      metrics.add(
          createMetric(user, HealthMetricType.STEPS, randomInRange(2000, 15000), recordedAt));
    }

    // Add a few one-time metrics (Height, BMI, HbA1c - less frequently tracked)
    metrics.add(
        createMetric(
            user,
            HealthMetricType.HEIGHT,
            randomInRange(150, 190),
            now.minus(30, ChronoUnit.DAYS)));
    metrics.add(
        createMetric(
            user, HealthMetricType.BMI, randomInRange(18.5, 28), now.minus(30, ChronoUnit.DAYS)));
    metrics.add(
        createMetric(
            user, HealthMetricType.HBA1C, randomInRange(4.5, 6.5), now.minus(60, ChronoUnit.DAYS)));

    healthMetricRepository.saveAll(metrics);
    log.info("Generated {} test health metrics for user: {}", metrics.size(), user.getId());
  }

  /**
   * Create a health metric entity with the given parameters
   *
   * @param user The user entity
   * @param metricType The type of health metric
   * @param value The metric value
   * @param recordedAt When the metric was recorded
   * @return HealthMetricEntity
   */
  private HealthMetricEntity createMetric(
      UserEntity user, HealthMetricType metricType, BigDecimal value, Instant recordedAt) {
    return HealthMetricEntity.builder()
        .user(user)
        .metricType(metricType)
        .value(value)
        .unit(metricType.getDefaultUnit())
        .recordedAt(recordedAt)
        .source("test_data")
        .notes("Auto-generated test data for development")
        .build();
  }

  /**
   * Generate a random BigDecimal within the specified range (inclusive)
   *
   * @param min Minimum value
   * @param max Maximum value
   * @return Random BigDecimal value
   */
  private BigDecimal randomInRange(double min, double max) {
    double value = min + (max - min) * random.nextDouble();
    return BigDecimal.valueOf(Math.round(value * 10.0) / 10.0); // Round to 1 decimal place
  }
}
