package com.kpi.composer.model.dto;

import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.validate.annotation.FormatSupported;
import com.kpi.composer.validate.annotation.SizeLimited;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@FormatSupported
@SizeLimited
public abstract class FileDto {

    private Long id;

    @NotBlank
    private String name;

    private SupportedFormats format;

    @PastOrPresent
    private ZonedDateTime created;

    private Long size;

    private byte[] bytes;
}
