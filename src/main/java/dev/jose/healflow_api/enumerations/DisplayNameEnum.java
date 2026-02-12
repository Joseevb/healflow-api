package dev.jose.healflow_api.enumerations;

import java.util.Arrays;
import java.util.Optional;

public interface DisplayNameEnum<E extends Enum<E>> {
  String getDisplayName();

  static <E extends Enum<E> & DisplayNameEnum<E>> E fromStringName(
      Class<E> enumClass, String name) {
    return Enum.valueOf(enumClass, name.toUpperCase());
  }

  static <E extends Enum<E> & DisplayNameEnum<E>> Optional<E> fromDisplayName(
      Class<E> enumClass, String displayName) {
    return Arrays.stream(enumClass.getEnumConstants())
        .filter(e -> e.getDisplayName().equalsIgnoreCase(displayName))
        .findFirst();
  }
}
