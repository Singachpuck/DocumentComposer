package com.kpi.composer.validate;

import com.kpi.composer.model.dto.DatasetDto;
import com.kpi.composer.model.dto.FileDto;
import com.kpi.composer.model.dto.TemplateDto;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.validate.annotation.FormatSupported;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FormatSupportedValidator implements ConstraintValidator<FormatSupported, FileDto> {

    @Override
    public boolean isValid(FileDto target, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (target instanceof TemplateDto t) {
            if (!Template.SUPPORTED_FORMATS.contains(t.getFormat())) {
                context.buildConstraintViolationWithTemplate("Unsupported template format: " + t.getFormat())
                        .addConstraintViolation();
                return false;
            }
        } else if (target instanceof DatasetDto d) {
            if (!Dataset.SUPPORTED_FORMATS.contains(d.getFormat())) {
                context.buildConstraintViolationWithTemplate("Unsupported dataset format: " + d.getFormat())
                        .addConstraintViolation();
                return false;
            }
        } else {
            context.buildConstraintViolationWithTemplate("Format is not available.")
                    .addConstraintViolation();
        }
        return true;
    }
}
