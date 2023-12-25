package com.kpi.composer.validate;

import com.kpi.composer.validate.annotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s.length() < 6) {
            context.buildConstraintViolationWithTemplate("Password should be at least 6 character long.")
                    .addConstraintViolation();
            return false;
        }
        if (s.length() > 50) {
            context.buildConstraintViolationWithTemplate("Max password size exceeded: " + 50)
                    .addConstraintViolation();
            return false;
        }
        if (!Pattern.compile("\\d").matcher(s).find()) {
            context.buildConstraintViolationWithTemplate("Password should contain at least one digit.")
                    .addConstraintViolation();
            return false;
        }
        if (!Pattern.compile("[!~#?&+\\-*/.]").matcher(s).find()) {
            context.buildConstraintViolationWithTemplate("Password should contain at least one special character.")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
