package com.kpi.composer.model.entities;

import com.kpi.composer.model.SupportedFormats;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
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

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    private List<ComposedDocument> documents;

    @PreRemove
    private void setNullOnDelete() {
        documents.forEach(doc -> doc.setTemplate(null));
    }
}
