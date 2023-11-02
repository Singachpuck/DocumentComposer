package com.kpi.composer.model.entities;

import com.kpi.composer.model.SupportedFormats;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Template extends FileEntity {

    public static final Set<SupportedFormats> SUPPORTED_FORMATS = Set.of(SupportedFormats.DOCX);

    @Column
    private String beginTokenPlaceholder;

    @Column
    private String endTokenPlaceholder;

    @Column
    private String beginEscapePlaceholder;

    @Column
    private String endEscapePlaceholder;
}
