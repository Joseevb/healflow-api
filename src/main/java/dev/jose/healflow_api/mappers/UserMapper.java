package dev.jose.healflow_api.mappers;

import dev.jose.healflow_api.api.models.AddMedicineToUserRequestDTO;
import dev.jose.healflow_api.api.models.UserMedicinesResponseDTO;
import dev.jose.healflow_api.persistence.entities.UserEntity;
import dev.jose.healflow_api.persistence.entities.UserMedicinesEntity;
import dev.jose.medicines.model.MedicineDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper extends BaseMapper {

  @Mapping(target = "id.userId", source = "request.userId")
  @Mapping(target = "id.medicineId", source = "request.medicineId")
  @Mapping(target = "user", source = "user")
  UserMedicinesEntity toUserMedicinesEntity(
      AddMedicineToUserRequestDTO request, MedicineDTO medicine, UserEntity user);

  @Mapping(target = "userId", source = "entity.id.userId")
  @Mapping(target = "medicineId", source = "entity.id.medicineId")
  @Mapping(target = "medicineName", source = "medicine.nameOfMedicine")
  UserMedicinesResponseDTO toUserMedicinesResponseDTO(
      UserMedicinesEntity entity, MedicineDTO medicine);
}
