package dev.jose.healflow_api.services;

import dev.jose.healflow_api.api.models.AddMedicineToUserRequestDTO;
import dev.jose.healflow_api.api.models.UpdateUserMedicineRequestDTO;
import dev.jose.healflow_api.api.models.UserMedicineCountResponseDTO;
import dev.jose.healflow_api.api.models.UserMedicinesResponseDTO;
import dev.jose.healflow_api.exceptions.ForbiddenException;
import dev.jose.healflow_api.exceptions.NotFoundException;
import dev.jose.healflow_api.mappers.UserMapper;
import dev.jose.healflow_api.persistence.entities.UserEntity;
import dev.jose.healflow_api.persistence.entities.UserMedicinesEntity;
import dev.jose.healflow_api.persistence.repositories.UserMedicinesRepository;
import dev.jose.healflow_api.persistence.repositories.UserRepository;
import dev.jose.medicines.model.MedicineDTO;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMedicineServiceImpl implements UserMedicineService {

  private final UserRepository userRepository;
  private final MedicineService medicineService;
  private final UserMedicinesRepository userMedicinesRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional(readOnly = true)
  public List<UserMedicinesResponseDTO> getUserMedicines(UUID userId) {
    log.info("Fetching medicines for user: {}", userId);
    return userRepository.findById(userId).map(userMedicinesRepository::findByUser).stream()
        .flatMap(Collection::stream)
        .map(
            entity -> {
              MedicineDTO medicine =
                  medicineService.getMedicineById(entity.getId().getMedicineId());
              return userMapper.toUserMedicinesResponseDTO(entity, medicine);
            })
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public UserMedicinesResponseDTO getUserMedicineById(
      UUID userId, Integer medicineId, UUID requestUserId) {
    log.info(
        "Fetching medicine {} for user {} (requested by {})", medicineId, userId, requestUserId);

    // Verify ownership
    if (!userId.equals(requestUserId)) {
      log.warn("User {} attempted to access medicines of user {}", requestUserId, userId);
      throw new ForbiddenException("You can only access your own medicines");
    }

    UserMedicinesEntity.UserMedicineId id = new UserMedicinesEntity.UserMedicineId();
    id.setUserId(userId);
    id.setMedicineId(medicineId);

    UserMedicinesEntity entity =
        userMedicinesRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "UserMedicine",
                        "userId=" + userId + ", medicineId=" + medicineId,
                        userId));

    MedicineDTO medicine = medicineService.getMedicineById(medicineId);
    return userMapper.toUserMedicinesResponseDTO(entity, medicine);
  }

  @Override
  @Transactional(readOnly = true)
  public UserMedicineCountResponseDTO getUserMedicineCount(UUID userId) {
    log.info("Fetching medicine count for user: {}", userId);

    // Verify user exists
    if (!userRepository.existsById(userId)) {
      throw new NotFoundException("User", "id", userId);
    }

    long count = userMedicinesRepository.countByUser_Id(userId);
    return UserMedicineCountResponseDTO.builder().count(count).build();
  }

  @Override
  @Transactional
  public UserMedicinesResponseDTO addMedicineToUser(
      UUID userId, AddMedicineToUserRequestDTO request) {
    log.info("Adding medicine {} to user {}", request.medicineId(), userId);

    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("User", "id", userId));

    MedicineDTO medicine = medicineService.getMedicineById(request.medicineId());

    // Create request with userId
    AddMedicineToUserRequestDTO requestWithUserId =
        AddMedicineToUserRequestDTO.builder()
            .userId(userId)
            .medicineId(request.medicineId())
            .dosage(request.dosage())
            .frequency(request.frequency())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .build();

    var entity = userMapper.toUserMedicinesEntity(requestWithUserId, medicine, user);
    userMedicinesRepository.save(entity);

    log.info("Successfully added medicine {} to user {}", request.medicineId(), userId);
    return userMapper.toUserMedicinesResponseDTO(entity, medicine);
  }

  @Override
  @Transactional
  public UserMedicinesResponseDTO updateUserMedicine(
      UUID userId, Integer medicineId, UUID requestUserId, UpdateUserMedicineRequestDTO request) {
    log.info(
        "Updating medicine {} for user {} (requested by {})", medicineId, userId, requestUserId);

    // Verify ownership
    if (!userId.equals(requestUserId)) {
      log.warn("User {} attempted to update medicines of user {}", requestUserId, userId);
      throw new ForbiddenException("You can only update your own medicines");
    }

    UserMedicinesEntity.UserMedicineId id = new UserMedicinesEntity.UserMedicineId();
    id.setUserId(userId);
    id.setMedicineId(medicineId);

    UserMedicinesEntity entity =
        userMedicinesRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "UserMedicine",
                        "userId=" + userId + ", medicineId=" + medicineId,
                        userId));

    // Update fields
    entity.setDosage(request.dosage());
    entity.setFrequency(request.frequency());
    entity.setStartDate(request.startDate());
    entity.setEndDate(request.endDate());

    userMedicinesRepository.save(entity);

    MedicineDTO medicine = medicineService.getMedicineById(medicineId);
    log.info("Successfully updated medicine {} for user {}", medicineId, userId);
    return userMapper.toUserMedicinesResponseDTO(entity, medicine);
  }

  @Override
  @Transactional
  public void deleteUserMedicine(UUID userId, Integer medicineId, UUID requestUserId) {
    log.info(
        "Deleting medicine {} for user {} (requested by {})", medicineId, userId, requestUserId);

    // Verify ownership
    if (!userId.equals(requestUserId)) {
      log.warn("User {} attempted to delete medicines of user {}", requestUserId, userId);
      throw new ForbiddenException("You can only delete your own medicines");
    }

    UserMedicinesEntity.UserMedicineId id = new UserMedicinesEntity.UserMedicineId();
    id.setUserId(userId);
    id.setMedicineId(medicineId);

    if (!userMedicinesRepository.existsById(id)) {
      throw new NotFoundException(
          "UserMedicine", "userId=" + userId + ", medicineId=" + medicineId, userId);
    }

    userMedicinesRepository.deleteById(id);
    log.info("Successfully deleted medicine {} for user {}", medicineId, userId);
  }
}
