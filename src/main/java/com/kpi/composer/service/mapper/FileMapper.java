package com.kpi.composer.service.mapper;

import com.kpi.composer.dto.DatasetDto;
import com.kpi.composer.dto.FileDto;
import com.kpi.composer.dto.TemplateDto;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.FileEntity;
import com.kpi.composer.model.entities.Template;
import org.mapstruct.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.ZonedDateTime;

@Mapper(
        componentModel = "spring",
        imports = { ZonedDateTime.class }
)
public interface FileMapper {

    FileDto paramsToDto(String name, SupportedFormats format);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "size", expression = "java(file.getSize())")
    Template dtoToTemplate(FileDto fileDto, @Context MultipartFile file);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "size", expression = "java(file.getSize())")
    Dataset dtoToDataset(FileDto fileDto, @Context MultipartFile file);

    TemplateDto templateToDto(Template template);

    DatasetDto datasetToDto(Dataset dataset);

    @AfterMapping
    default void afterDtoToFile(@MappingTarget FileEntity file, @Context MultipartFile multipartFile) {
        try {
            file.setBytes(multipartFile.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing file.");
        }
    }
}
