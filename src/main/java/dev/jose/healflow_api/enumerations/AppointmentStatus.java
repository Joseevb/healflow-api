package dev.jose.healflow_api.enumerations;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(enumAsRef = true)
@RequiredArgsConstructor
public enum AppointmentStatus implements DisplayNameEnum<AppointmentStatus> {
  PENDING("Pending"),
  CONFIRMED("Confirmed"),
  COMPLETED("Completed"),
  CANCELLED("Cancelled"),
  NO_SHOW("No Show");

  @Getter private final String displayName;
}
