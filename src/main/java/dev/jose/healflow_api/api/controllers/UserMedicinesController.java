package dev.jose.healflow_api.api.controllers;

import dev.jose.healflow_api.api.docs.UserMedicinesApi;
import dev.jose.healflow_api.api.models.AddMedicineToUserRequestDTO;
import dev.jose.healflow_api.api.models.UpdateUserMedicineRequestDTO;
import dev.jose.healflow_api.api.models.UserMedicineCountResponseDTO;
import dev.jose.healflow_api.api.models.UserMedicinesResponseDTO;
import dev.jose.healflow_api.services.UserMedicineService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class UserMedicinesController implements UserMedicinesApi {

  private final UserMedicineService userMedicineService;

  @Override
  public ResponseEntity<List<UserMedicinesResponseDTO>> getUserMedicines(UUID userId) {
    return ResponseEntity.ok(userMedicineService.getUserMedicines(userId));
  }

  @Override
  public ResponseEntity<UserMedicinesResponseDTO> getUserMedicineById(
      UUID userId, Integer medicineId, UUID requestUserId) {
    return ResponseEntity.ok(
        userMedicineService.getUserMedicineById(userId, medicineId, requestUserId));
  }

  @Override
  public ResponseEntity<UserMedicineCountResponseDTO> getUserMedicineCount(UUID userId) {
    return ResponseEntity.ok(userMedicineService.getUserMedicineCount(userId));
  }

  @Override
  public ResponseEntity<UserMedicinesResponseDTO> addMedicineToUser(
      UUID userId, AddMedicineToUserRequestDTO request, UriComponentsBuilder uriBuilder) {
    UserMedicinesResponseDTO response = userMedicineService.addMedicineToUser(userId, request);

    URI location =
        uriBuilder
            .path("/user-medicines/{userId}/{medicineId}")
            .buildAndExpand(response.userId(), response.medicineId())
            .toUri();

    return ResponseEntity.created(location).body(response);
  }

  @Override
  public ResponseEntity<UserMedicinesResponseDTO> updateUserMedicine(
      UUID userId, Integer medicineId, UUID requestUserId, UpdateUserMedicineRequestDTO request) {
    return ResponseEntity.ok(
        userMedicineService.updateUserMedicine(userId, medicineId, requestUserId, request));
  }

  @Override
  public ResponseEntity<Void> deleteUserMedicine(
      UUID userId, Integer medicineId, UUID requestUserId) {
    userMedicineService.deleteUserMedicine(userId, medicineId, requestUserId);
    return ResponseEntity.noContent().build();
  }
}
