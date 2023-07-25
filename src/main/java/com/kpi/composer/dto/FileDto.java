package com.kpi.composer.dto;

import com.kpi.composer.model.SupportedFormats;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class FileDto {

    private Long id;

    private String name;

    private SupportedFormats format;

    private ZonedDateTime created;

    private long size;
}
