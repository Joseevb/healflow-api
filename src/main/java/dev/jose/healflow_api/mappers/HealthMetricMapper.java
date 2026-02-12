package dev.jose.healflow_api.mappers;

import dev.jose.healflow_api.api.models.CreateHealthMetricRequestDTO;
import dev.jose.healflow_api.api.models.HealthMetricResponseDTO;
import dev.jose.healflow_api.api.models.HealthMetricSummaryDTO;
import dev.jose.healflow_api.api.models.UpdateHealthMetricRequestDTO;
import dev.jose.healflow_api.persistence.entities.HealthMetricEntity;
import dev.jose.healflow_api.persistence.entities.UserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface HealthMetricMapper {

  @Mapping(target = "id", source = "entity.id")
  @Mapping(target = "metricType", source = "entity.metricType")
  @Mapping(target = "value", source = "entity.value")
  @Mapping(target = "unit", source = "entity.unit")
  @Mapping(target = "recordedAt", source = "entity.recordedAt")
  @Mapping(target = "notes", source = "entity.notes")
  @Mapping(target = "source", source = "entity.source")
  @Mapping(target = "recordedBySpecialistId", source = "entity.recordedBySpecialistId")
  @Mapping(target = "createdAt", source = "entity.createdAt")
  @Mapping(target = "updatedAt", source = "entity.updatedAt")
  HealthMetricResponseDTO toDto(HealthMetricEntity entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", source = "user")
  @Mapping(target = "metricType", source = "dto.metricType")
  @Mapping(target = "value", source = "dto.value")
  @Mapping(target = "unit", source = "dto.unit")
  @Mapping(target = "recordedAt", source = "dto.recordedAt")
  @Mapping(target = "notes", source = "dto.notes")
  @Mapping(target = "source", source = "dto.source")
  @Mapping(target = "recordedBySpecialistId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  HealthMetricEntity toEntity(CreateHealthMetricRequestDTO dto, UserEntity user);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "metricType", ignore = true)
  @Mapping(target = "source", ignore = true)
  @Mapping(target = "recordedBySpecialistId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  HealthMetricEntity updateEntity(
      @MappingTarget HealthMetricEntity entity, UpdateHealthMetricRequestDTO dto);

  @Mapping(target = "metricType", source = "metricType")
  @Mapping(target = "latestValue", source = "value")
  @Mapping(target = "unit", source = "unit")
  @Mapping(target = "recordedAt", source = "recordedAt")
  @Mapping(target = "trend", constant = "insufficient_data")
  HealthMetricSummaryDTO toSummaryDto(HealthMetricEntity entity);
}
