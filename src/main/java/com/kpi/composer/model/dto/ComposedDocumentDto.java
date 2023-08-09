package com.kpi.composer.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComposedDocumentDto extends FileDto {

    private Long templateId;

    private Long datasetId;
}
