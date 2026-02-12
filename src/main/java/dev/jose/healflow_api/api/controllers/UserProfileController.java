package dev.jose.healflow_api.api.controllers;

import dev.jose.healflow_api.api.docs.UserProfileApi;
import dev.jose.healflow_api.api.models.UpdateUserProfileRequestDTO;
import dev.jose.healflow_api.api.models.UserProfileResponseDTO;
import dev.jose.healflow_api.services.UserProfileService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserProfileController implements UserProfileApi {

  private final UserProfileService userProfileService;

  @Override
  public ResponseEntity<UserProfileResponseDTO> getUserProfile() {
    UUID authId = getUserIdFromAuth();
    log.info("Getting profile for user: {}", authId);
    return ResponseEntity.ok(userProfileService.getUserProfile(authId));
  }

  @Override
  public ResponseEntity<UserProfileResponseDTO> updateUserProfile(
      UpdateUserProfileRequestDTO body) {
    UUID authId = getUserIdFromAuth();
    log.info("Updating profile for user: {}", authId);
    return ResponseEntity.ok(userProfileService.updateUserProfile(authId, body));
  }

  private UUID getUserIdFromAuth() {
    Authentication authentication =
        org.springframework.security.core.context.SecurityContextHolder.getContext()
            .getAuthentication();
    String userId = authentication.getName();
    return UUID.fromString(userId);
  }
}
