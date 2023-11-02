package com.kpi.composer.model.dto;

import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.validate.annotation.FormatSupported;
import com.kpi.composer.validate.annotation.SizeLimited;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@FormatSupported
@SizeLimited
public abstract class FileDto {

    private Long id;

    @NotBlank(message = "Entity name must not be blank.")
    @Size(max = 50, message = "Max entity name size is 50.")
    private String name;

    private SupportedFormats format;

    @PastOrPresent(message = "Invalid creation date.")
    private ZonedDateTime created;

    private Long size;

    private byte[] bytes;
}
