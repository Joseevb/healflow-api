package dev.jose.healflow_api.api.models;

import dev.jose.healflow_api.enumerations.SpecialistTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SpecialistTypeResponse", description = "Specialist type")
public record SpecialistTypeResponseDTO(SpecialistTypeEnum type) {}
