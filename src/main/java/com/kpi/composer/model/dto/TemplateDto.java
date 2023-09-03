package com.kpi.composer.model.dto;


import com.kpi.composer.validate.annotation.FormatSupported;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplateDto extends FileDto {

    @NotBlank(message = "Placeholder must not be blank.")
    private String beginTokenPlaceholder;

    @NotBlank(message = "Placeholder must not be blank.")
    private String endTokenPlaceholder;

    @NotBlank(message = "Placeholder must not be blank.")
    private String beginEscapePlaceholder;

    @NotBlank(message = "Placeholder must not be blank.")
    private String endEscapePlaceholder;
}
