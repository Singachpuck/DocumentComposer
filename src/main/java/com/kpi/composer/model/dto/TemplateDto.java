package com.kpi.composer.model.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplateDto extends FileDto {

    @Size(max = 10, message = "Max placeholder size is 10.")
    @NotBlank(message = "Placeholder must not be blank.")
    private String beginTokenPlaceholder;

    @Size(max = 10, message = "Max placeholder size is 10.")
    @NotBlank(message = "Placeholder must not be blank.")
    private String endTokenPlaceholder;

    @Size(max = 10, message = "Max placeholder size is 10.")
    @NotBlank(message = "Placeholder must not be blank.")
    private String beginEscapePlaceholder;

    @Size(max = 10, message = "Max placeholder size is 10.")
    @NotBlank(message = "Placeholder must not be blank.")
    private String endEscapePlaceholder;
}
