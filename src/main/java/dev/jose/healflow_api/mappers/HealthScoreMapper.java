package dev.jose.healflow_api.mappers;

import dev.jose.healflow_api.api.models.HealthScoreResponseDTO;
import dev.jose.healflow_api.persistence.entities.HealthScoreEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface HealthScoreMapper {

  @Mapping(target = "recentMetrics", ignore = true)
  @Mapping(target = "recommendations", ignore = true)
  HealthScoreResponseDTO toDto(HealthScoreEntity entity);
}
