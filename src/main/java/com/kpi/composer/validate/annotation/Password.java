package com.kpi.composer.validate.annotation;

import com.kpi.composer.validate.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Password is not valid.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
