package com.kpi.composer.validate.annotation;

import com.kpi.composer.validate.FormatSupportedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FormatSupportedValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FormatSupported {

    String message() default "Format is not supported.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
