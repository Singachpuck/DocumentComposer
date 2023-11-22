package com.kpi.composer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.validate.annotation.FormatSupported;
import com.kpi.composer.validate.annotation.SizeLimited;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@FormatSupported
@SizeLimited
public abstract class FileDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Entity name must not be blank.")
    @Size(max = 50, message = "Max entity name size is 50.")
    private String name;

    private SupportedFormats format;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime created;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long size;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] bytes;
}
