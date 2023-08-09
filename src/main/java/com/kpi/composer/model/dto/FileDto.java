package com.kpi.composer.model.dto;

import com.kpi.composer.model.SupportedFormats;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public abstract class FileDto {

    private Long id;

    private String name;

    private SupportedFormats format;

    private ZonedDateTime created;

    private Long size;

    private byte[] bytes;
}
