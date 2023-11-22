package com.kpi.composer.service.mapper;

import com.kpi.composer.model.dto.ComposedDocumentDto;
import com.kpi.composer.model.dto.DatasetDto;
import com.kpi.composer.model.dto.TemplateDto;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.model.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.ZonedDateTime;

@Mapper(
        componentModel = "spring",
        imports = { ZonedDateTime.class }
)
public interface FileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "bytes", expression = "java(fileDto.getBytes())")
    @Mapping(target = "size", expression = "java(fileDto.getBytes().length)")
    Template dtoToTemplate(TemplateDto fileDto, User owner);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "bytes", expression = "java(fileDto.getBytes())")
    @Mapping(target = "size", expression = "java(fileDto.getBytes().length)")
    Dataset dtoToDataset(DatasetDto fileDto, User owner);

    @Mapping(target = "bytes", ignore = true)
    TemplateDto templateToDto(Template template);

    @Mapping(target = "bytes", ignore = true)
    DatasetDto datasetToDto(Dataset dataset);

    @Mapping(target = "bytes", ignore = true)
    @Mapping(target = "templateId", source = "template.id")
    @Mapping(target = "datasetId", source = "dataset.id")
    ComposedDocumentDto composedToDto(ComposedDocument composedDocument);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "format", ignore = true)
    @Mapping(target = "name", expression = "java(template.getName() + \" - \" + dataset.getName())")
    @Mapping(target = "created", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "bytes", expression = "java(bytes)")
    @Mapping(target = "size", expression = "java(bytes.length)")
    @Mapping(target = "owner", ignore = true)
    ComposedDocument documentFromParams(Template template, Dataset dataset, byte[] bytes);
}
