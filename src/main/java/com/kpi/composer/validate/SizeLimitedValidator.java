package com.kpi.composer.validate;

import com.kpi.composer.model.dto.DatasetDto;
import com.kpi.composer.model.dto.FileDto;
import com.kpi.composer.model.dto.TemplateDto;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.validate.annotation.FormatSupported;
import com.kpi.composer.validate.annotation.SizeLimited;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

public class SizeLimitedValidator implements ConstraintValidator<SizeLimited, FileDto> {

    @Value("${entity.template.max-size}")
    private long templateMaxSize;

    @Value("${entity.dataset.max-size}")
    private long datasetMaxSize;

    @Override
    public boolean isValid(FileDto target, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (target.getBytes() == null || target.getBytes().length == 0) {
            context.buildConstraintViolationWithTemplate("Content is not sent.")
                    .addConstraintViolation();
            return false;
        }

        if (target instanceof TemplateDto t) {
            if (t.getBytes().length > templateMaxSize) {
                context.buildConstraintViolationWithTemplate("Max template size exceeded: " + templateMaxSize)
                        .addConstraintViolation();
                return false;
            }
        } else if (target instanceof DatasetDto d) {
            if (d.getBytes().length > datasetMaxSize) {
                context.buildConstraintViolationWithTemplate("Max dataset size exceeded: " + datasetMaxSize)
                        .addConstraintViolation();
                return false;
            }
        } else {
            context.buildConstraintViolationWithTemplate("Size is not available.")
                    .addConstraintViolation();
        }
        return true;
    }
}
