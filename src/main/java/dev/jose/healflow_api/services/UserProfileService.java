package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.UpdateUserProfileRequestDTO;
import dev.jose.healflow_api.api.models.UserProfileResponseDTO;
import java.util.UUID;

public interface UserProfileService {

  /**
   * Get user profile by auth ID
   *
   * @param authId the authentication user ID
   * @return user profile information
   */
  UserProfileResponseDTO getUserProfile(UUID authId);

  /**
   * Update user profile
   *
   * @param authId the authentication user ID
   * @param request the update request
   * @return updated user profile
   */
  UserProfileResponseDTO updateUserProfile(UUID authId, UpdateUserProfileRequestDTO request);

  /**
   * Check if user profile is complete
   *
   * @param authId the authentication user ID
   * @return true if profile is complete
   */
  boolean isProfileComplete(UUID authId);
}
