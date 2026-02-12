package dev.jose.healflow_api.enumerations;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@RequiredArgsConstructor
public enum SpecialistTypeEnum implements DisplayNameEnum<SpecialistTypeEnum> {
  CARDIOLOGY("Cardiology"),
  DERMATOLOGY("Dermatology"),
  GENERAL_PRACTICE("General Practice"),
  DENTISTRY("Dentistry");

  @Getter private final String displayName;
}
