package dev.jose.healflow_api.services;

import static java.util.function.Predicate.not;

import dev.jose.healflow_api.api.models.SpecialistSummaryDTO;
import dev.jose.healflow_api.api.models.UpdateUserProfileRequestDTO;
import dev.jose.healflow_api.api.models.UserProfileResponseDTO;
import dev.jose.healflow_api.exceptions.NotFoundException;
import dev.jose.healflow_api.persistence.entities.UserEntity;
import dev.jose.healflow_api.persistence.repositories.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

  private final UserRepository userRepository;

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
