package dev.jose.healflow_api.mappers;

import dev.jose.healflow_api.api.models.SpecialistResponseDTO;
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
}
