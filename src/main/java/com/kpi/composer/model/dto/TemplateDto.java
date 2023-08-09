package com.kpi.composer.model.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplateDto extends FileDto {

    private String beginTokenPlaceholder;

    private String endTokenPlaceholder;

    private String beginEscapePlaceholder;

    private String endEscapePlaceholder;
}
