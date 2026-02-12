package dev.jose.healflow_api.exceptions;

import java.util.List;
import java.util.UUID;
import lombok.Getter;

public class AuthUserIdValidationException extends RuntimeException {

  @Getter private final List<UUID> invalidIds;

  public AuthUserIdValidationException(List<UUID> invalidIds) {
    super(
        "Invalid auth user ids: "
            + invalidIds.stream().map(UUID::toString).reduce((a, b) -> a + " " + b).orElse(""));
    this.invalidIds = invalidIds;
  }
}
