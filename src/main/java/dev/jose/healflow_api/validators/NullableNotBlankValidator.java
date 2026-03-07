package dev.jose.healflow_api.validators;

import dev.jose.healflow_api.annotations.NullableNotBlank;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableNotBlankValidator implements ConstraintValidator<NullableNotBlank, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    // null is valid, non-null must be not blank
    return value == null || !value.isBlank();
  }
}
