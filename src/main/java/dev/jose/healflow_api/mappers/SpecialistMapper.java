package dev.jose.healflow_api.mappers;

import dev.jose.healflow_api.api.models.CreateSpecialistRequestDTO;
import dev.jose.healflow_api.api.models.SpecialistAvailabilityResponseDTO;
import dev.jose.healflow_api.api.models.SpecialistResponseDTO;
import dev.jose.healflow_api.persistence.entities.SpecialistAvailabilityEntity;
import dev.jose.healflow_api.persistence.entities.SpecialistEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface SpecialistMapper extends BaseMapper {

  @Mapping(target = "name", source = "fullName")
  SpecialistResponseDTO toDto(SpecialistEntity entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  SpecialistEntity toEntity(CreateSpecialistRequestDTO dto);

  @Mapping(target = "specialistId", source = "specialist.id")
  SpecialistAvailabilityResponseDTO toAvailabilityDto(SpecialistAvailabilityEntity entity);

  SpecialistAvailabilityEntity toAvailabilityEntity(SpecialistAvailabilityResponseDTO dto);
}
