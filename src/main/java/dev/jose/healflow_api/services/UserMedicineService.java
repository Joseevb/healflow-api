package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.AddMedicineToUserRequestDTO;
import dev.jose.healflow_api.api.models.UpdateUserMedicineRequestDTO;
import dev.jose.healflow_api.api.models.UserMedicineCountResponseDTO;
import dev.jose.healflow_api.api.models.UserMedicinesResponseDTO;
import java.util.List;
import java.util.UUID;

public interface UserMedicineService {

  List<UserMedicinesResponseDTO> getUserMedicines(UUID userId);

  UserMedicinesResponseDTO getUserMedicineById(UUID userId, Integer medicineId, UUID requestUserId);

  UserMedicineCountResponseDTO getUserMedicineCount(UUID userId);

  UserMedicinesResponseDTO addMedicineToUser(
      UUID userId, AddMedicineToUserRequestDTO request);

  UserMedicinesResponseDTO updateUserMedicine(
      UUID userId, Integer medicineId, UUID requestUserId, UpdateUserMedicineRequestDTO request);

  void deleteUserMedicine(UUID userId, Integer medicineId, UUID requestUserId);
}
