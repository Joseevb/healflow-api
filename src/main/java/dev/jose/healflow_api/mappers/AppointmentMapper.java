package dev.jose.healflow_api.mappers;

import dev.jose.healflow_api.api.models.AppointmentResponseDTO;
import dev.jose.healflow_api.api.models.ClientSummaryDTO;
import dev.jose.healflow_api.api.models.CreateAppointmentRequestDTO;
import dev.jose.healflow_api.api.models.SpecialistSummaryDTO;
import dev.jose.healflow_api.api.models.UpdateAppointmentRequestDTO;
import dev.jose.healflow_api.persistence.entities.AppointmentEntity;
import dev.jose.healflow_api.persistence.entities.SpecialistEntity;
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
    uses = {UserMapper.class, SpecialistMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AppointmentMapper {

  @Mapping(target = "client", source = "client")
  @Mapping(target = "specialist", source = "specialist")
  AppointmentResponseDTO toDto(AppointmentEntity entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "client", source = "client")
  @Mapping(target = "specialist", source = "specialist")
  @Mapping(target = "status", constant = "PENDING")
  @Mapping(target = "durationMinutes", source = "specialist.consultationDurationMinutes")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  AppointmentEntity toEntity(
      CreateAppointmentRequestDTO dto, UserEntity client, SpecialistEntity specialist);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "client", ignore = true)
  @Mapping(target = "specialist", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "durationMinutes", ignore = true)
  AppointmentEntity updateEntity(
      @MappingTarget AppointmentEntity entity, UpdateAppointmentRequestDTO dto);

  @Mapping(
      target = "name",
      expression = "java(client.getFirstName() + \" \" + client.getLastName())")
  ClientSummaryDTO toClientSummary(UserEntity client);

  @Mapping(target = "name", source = "fullName")
  SpecialistSummaryDTO toSpecialistSummary(SpecialistEntity specialist);
}
