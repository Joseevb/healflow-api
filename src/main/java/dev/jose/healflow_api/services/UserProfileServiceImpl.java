package dev.jose.healflow_api.services;

import static java.util.function.Predicate.not;

import dev.jose.healflow_api.api.models.AdminCreateUserRequestDTO;
import dev.jose.healflow_api.api.models.AdminUserProfileResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistSummaryDTO;
import dev.jose.healflow_api.api.models.UpdateUserProfileRequestDTO;
import dev.jose.healflow_api.api.models.UserProfileResponseDTO;
import dev.jose.healflow_api.exceptions.ConflictException;
import dev.jose.healflow_api.exceptions.NotFoundException;
import dev.jose.healflow_api.mappers.UserMapper;
import dev.jose.healflow_api.persistence.entities.UserEntity;
import dev.jose.healflow_api.persistence.repositories.SpecialistRepository;
import dev.jose.healflow_api.persistence.repositories.UserRepository;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

  private final UserMapper userMapper;
  private final UserRepository userRepository;
  private final SpecialistRepository specialistRepository;

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 20;

  @Override
  @Transactional(readOnly = true)
  public UserProfileResponseDTO getUserProfile(UUID authId) {
    UserEntity user =
        userRepository
            .findByAuthId(authId)
            .orElseThrow(() -> new NotFoundException("User", "auth_id", authId));

    return toProfileResponse(user);
  }

  @Override
  @Transactional
  public UserProfileResponseDTO updateUserProfile(
      UUID authId, UpdateUserProfileRequestDTO request) {
    UserEntity user =
        userRepository
            .findByAuthId(authId)
            .orElseThrow(() -> new NotFoundException("User", "auth_id", authId));

    // Update fields if provided

    Optional.ofNullable(request.firstName())
        .filter(not(String::isBlank))
        .ifPresent(user::setFirstName);

    Optional.ofNullable(request.lastName())
        .filter(not(String::isBlank))
        .ifPresent(user::setLastName);

    Optional.ofNullable(request.phone()).filter(not(String::isBlank)).ifPresent(user::setPhone);

    Optional.ofNullable(request.dateOfBirth()).ifPresent(user::setDateOfBirth);

    UserEntity updatedUser = userRepository.save(user);
    log.info("Updated profile for user: {}", authId);

    return toProfileResponse(updatedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isProfileComplete(UUID authId) {
    UserEntity user =
        userRepository
            .findByAuthId(authId)
            .orElseThrow(() -> new NotFoundException("User", "auth_id", authId));

    return isProfileComplete(user);
  }

  /**
   * Check if user profile is complete Profile is considered complete if: - First name is not "User"
   * (the default) - Phone is provided - Date of birth is provided
   */
  private boolean isProfileComplete(UserEntity user) {
    boolean hasRealName = user.getFirstName() != null && !"User".equals(user.getFirstName());
    boolean hasPhone = user.getPhone() != null && !user.getPhone().isBlank();
    boolean hasDateOfBirth = user.getDateOfBirth() != null;

    return hasRealName && hasPhone && hasDateOfBirth;
  }

  @Override
  @Transactional
  public UserProfileResponseDTO adminCreateOrUpdateUser(AdminCreateUserRequestDTO request) {
    log.info("Admin creating/updating user with auth ID: {}", request.authId());

    // Check if specialist exists
    var specialist =
        specialistRepository
            .findById(request.primarySpecialistId())
            .orElseThrow(
                () -> new NotFoundException("Specialist", "id", request.primarySpecialistId()));

    UserEntity user;

    // Check if user already exists by authId
    var existingUser = userRepository.findByAuthId(request.authId());

    if (existingUser.isPresent()) {
      user = existingUser.get();
      log.info("Updating existing user with auth ID: {}", request.authId());

      // Check if email is being changed and if new email already exists
      if (!user.getEmail().equals(request.email())
          && userRepository.existsByEmail(request.email())) {
        throw new ConflictException("Email already exists");
      }

      user.setEmail(request.email());
      user.setFirstName(request.firstName());
      user.setLastName(request.lastName());
      user.setPrimarySpecialist(specialist);

      Optional.ofNullable(request.phone()).filter(not(String::isBlank)).ifPresent(user::setPhone);
      Optional.ofNullable(request.dateOfBirth()).ifPresent(user::setDateOfBirth);

      if (request.isSubscribed() != null) {
        user.setIsSubscribed(request.isSubscribed());
      }
      if (request.isActive() != null) {
        user.setIsActive(request.isActive());
      }
    } else {
      // Create new user
      log.info("Creating new user with auth ID: {}", request.authId());

      // Check if email already exists
      if (userRepository.existsByEmail(request.email())) {
        throw new ConflictException("Email already exists");
      }

      // Check if authId already exists
      if (userRepository.existsByAuthId(request.authId())) {
        throw new ConflictException("Auth ID already exists");
      }

      user =
          UserEntity.builder()
              .authId(request.authId())
              .email(request.email())
              .firstName(request.firstName())
              .lastName(request.lastName())
              .phone(request.phone())
              .dateOfBirth(request.dateOfBirth())
              .primarySpecialist(specialist)
              .isSubscribed(request.isSubscribed() != null ? request.isSubscribed() : false)
              .isActive(request.isActive() != null ? request.isActive() : true)
              .build();
    }

    UserEntity savedUser = userRepository.save(user);
    log.info("Successfully created/updated user with auth ID: {}", request.authId());

    return toProfileResponse(savedUser);
  }

  @Override
  public AdminUserProfileResponseDTO getAllUsers(Integer page, Integer pageSize) {
    return Optional.of(
            PageRequest.of(
                Objects.requireNonNullElse(page, DEFAULT_PAGE),
                Objects.requireNonNullElse(pageSize, DEFAULT_SIZE)))
        .map(userRepository::findAll)
        .map(
            p ->
                new AdminUserProfileResponseDTO(
                    p.getContent().stream().map(userMapper::toAdminUserProfileDTO).toList(),
                    p.getTotalPages()))
        .get();
  }

  @Override
  public void deleteUser(UUID id) {
    userRepository.findByAuthId(id).ifPresent(userRepository::delete);
  }

  private UserProfileResponseDTO toProfileResponse(UserEntity user) {
    var specialist = user.getPrimarySpecialist();
    var specialistSummary =
        SpecialistSummaryDTO.builder()
            .id(specialist.getId())
            .name(specialist.getFirstName() + " " + specialist.getLastName())
            .specialty(specialist.getSpecialty())
            .build();

    return UserProfileResponseDTO.builder()
        .id(user.getId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .phone(user.getPhone())
        .dateOfBirth(user.getDateOfBirth())
        .primarySpecialist(specialistSummary)
        .isProfileComplete(isProfileComplete(user))
        .build();
  }
}
