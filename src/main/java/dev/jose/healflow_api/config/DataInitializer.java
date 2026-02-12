package dev.jose.healflow_api.config;

import dev.jose.healflow_api.enumerations.AppointmentStatus;
import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import dev.jose.healflow_api.persistence.entities.AppointmentEntity;
import dev.jose.healflow_api.persistence.entities.SpecialistAvailabilityEntity;
import dev.jose.healflow_api.persistence.entities.SpecialistEntity;
import dev.jose.healflow_api.persistence.entities.UserEntity;
import dev.jose.healflow_api.persistence.repositories.AppointmentRepository;
import dev.jose.healflow_api.persistence.repositories.SpecialistAvailabilityRepository;
import dev.jose.healflow_api.persistence.repositories.SpecialistRepository;
import dev.jose.healflow_api.persistence.repositories.UserRepository;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

  private final TransactionTemplate transactionTemplate;

  @Bean
  @Profile("dev")
  CommandLineRunner initDatabase(
      UserRepository userRepository,
      SpecialistRepository specialistRepository,
      SpecialistAvailabilityRepository availabilityRepository,
      AppointmentRepository appointmentRepository) {

    return _ ->
        transactionTemplate.executeWithoutResult(
            status -> {
              log.info("Initializing database with mock data...");

              log.info("Users checked/created");

              log.info("Specialist types checked/created");

              // --- Create Specialists ---
              SpecialistEntity drJohnson =
                  createOrFetchSpecialist(
                      specialistRepository,
                      "sarah.johnson@hospital.com",
                      "Sarah",
                      "Johnson",
                      SpecialistTypeEnum.CARDIOLOGY,
                      "johnson.jpeg");

              SpecialistEntity drChen =
                  createOrFetchSpecialist(
                      specialistRepository,
                      "michael.chen@dental.com",
                      "Michael",
                      "Chen",
                      SpecialistTypeEnum.DENTISTRY,
                      "michael_chen.jpeg");

              SpecialistEntity drBrown =
                  createOrFetchSpecialist(
                      specialistRepository,
                      "emily.brown@clinic.com",
                      "Emily",
                      "Brown",
                      SpecialistTypeEnum.GENERAL_PRACTICE,
                      "emily_brown.jpeg");

              SpecialistEntity drPerez =
                  createOrFetchSpecialist(
                      specialistRepository,
                      "emily.perez@clinic.com",
                      "Emily",
                      "Perez",
                      SpecialistTypeEnum.DERMATOLOGY,
                      "emily_perez.jpeg");

              log.info("Specialists checked/created");

              var specialists = List.of(drJohnson, drChen, drBrown, drPerez);

              // --- Create Availabilities ---
              if (availabilityRepository.count() == 0) {
                List<SpecialistAvailabilityEntity> availabilities = new ArrayList<>();
                for (DayOfWeek day : DayOfWeek.values()) {
                  if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) continue;

                  // Dr. Johnson
                  availabilities.add(buildAvailability(drJohnson, day, 9, 12));
                  availabilities.add(buildAvailability(drJohnson, day, 13, 17));

                  // Dr. Chen
                  availabilities.add(buildAvailability(drChen, day, 9, 17));

                  // Dr. Brown
                  availabilities.add(buildAvailability(drBrown, day, 9, 17));

                  // Dr. Perez
                  availabilities.add(buildAvailability(drPerez, day, 9, 14));
                }
                availabilityRepository.saveAll(availabilities);
                log.info("Created {} availability slots", availabilities.size());
              }

              // --- Create Users ---
              UserEntity user1 =
                  createOrFetchUser(
                      userRepository,
                      "john.doe@email.com",
                      "John",
                      "Doe",
                      specialists.stream()
                          .filter(
                              specialist ->
                                  specialist
                                      .getSpecialty()
                                      .equals(SpecialistTypeEnum.GENERAL_PRACTICE))
                          .toList()
                          .getFirst());
              UserEntity user2 =
                  createOrFetchUser(
                      userRepository,
                      "jane.smith@email.com",
                      "Jane",
                      "Smith",
                      specialists.stream()
                          .filter(
                              specialist ->
                                  specialist
                                      .getSpecialty()
                                      .equals(SpecialistTypeEnum.GENERAL_PRACTICE))
                          .toList()
                          .getLast());

              // --- Create Appointments ---
              if (appointmentRepository.count() == 0) {
                List<AppointmentEntity> appointments = new ArrayList<>();
                Random random = new Random();
                Instant now = Instant.now();

                // Past appointments
                for (int i = 1; i <= 5; i++) {
                  // ... (your existing date logic)
                  Instant pastDate = now.minus(i * 7L, ChronoUnit.DAYS);
                  // simplified logic for brevity, ensuring the entity references are managed
                  appointments.add(
                      AppointmentEntity.builder()
                          .client(user1) // user1 is managed
                          .specialist(random.nextBoolean() ? drJohnson : drBrown) // managed
                          .appointmentDate(pastDate)
                          .durationMinutes((short) 30)
                          .status(AppointmentStatus.COMPLETED)
                          .notes("Regular checkup")
                          .build());

                  appointments.add(
                      AppointmentEntity.builder()
                          .client(user2) // user1 is managed
                          .specialist(random.nextBoolean() ? drPerez : drBrown) // managed
                          .appointmentDate(pastDate)
                          .durationMinutes((short) 30)
                          .status(AppointmentStatus.COMPLETED)
                          .notes("Regular checkup")
                          .build());
                }

                // Add future appointments logic here...

                appointmentRepository.saveAll(appointments);
                log.info("Created appointments");
              }

              log.info("Database initialization completed successfully!");
            });
  }

  // --- Helper Methods to ensure we always have a Managed Entity ---

  private UserEntity createOrFetchUser(
      UserRepository repo, String email, String first, String last, SpecialistEntity specialist) {
    return repo.findByEmail(email)
        .orElseGet(
            () ->
                repo.save(
                    UserEntity.builder()
                        .email(email)
                        .firstName(first)
                        .lastName(last)
                        .phone("+1-555-0000")
                        .dateOfBirth(Instant.now())
                        .authId(UUID.randomUUID())
                        .primarySpecialist(specialist)
                        .build()));
  }

  private SpecialistEntity createOrFetchSpecialist(
      SpecialistRepository repo,
      String email,
      String first,
      String last,
      SpecialistTypeEnum type,
      String profilePictureName) {
    return repo.findByEmail(email)
        .orElseGet(
            () ->
                repo.save(
                    SpecialistEntity.builder()
                        .firstName(first)
                        .lastName(last)
                        .email(email)
                        .phone("+1-555-0000")
                        .licenseNumber(UUID.randomUUID().toString().substring(0, 8))
                        .specialty(type)
                        .consultationDurationMinutes((short) 30)
                        .profilePictureName(profilePictureName)
                        .build()));
  }

  private SpecialistAvailabilityEntity buildAvailability(
      SpecialistEntity specialist, DayOfWeek day, int startHour, int endHour) {
    return SpecialistAvailabilityEntity.builder()
        .specialist(specialist)
        .dayOfWeek(day)
        .startTime(LocalTime.of(startHour, 0))
        .endTime(LocalTime.of(endHour, 0))
        .build();
  }
}
