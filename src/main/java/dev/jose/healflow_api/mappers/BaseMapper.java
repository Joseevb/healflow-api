package dev.jose.healflow_api.mappers;

import java.util.Optional;

public interface BaseMapper {

  default <T> T map(Optional<T> opt) {
    return opt.orElse(null);
  }
}
