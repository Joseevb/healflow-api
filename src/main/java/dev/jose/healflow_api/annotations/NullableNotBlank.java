package dev.jose.healflow_api.annotations;

import dev.jose.healflow_api.validators.NullableNotBlankValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NullableNotBlankValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NullableNotBlank {
  String message() default "Field cannot be blank when provided";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
