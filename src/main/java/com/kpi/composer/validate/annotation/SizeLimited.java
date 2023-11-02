package com.kpi.composer.validate.annotation;

import com.kpi.composer.validate.SizeLimitedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SizeLimitedValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SizeLimited {
    String message() default "Max size exceeded.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
