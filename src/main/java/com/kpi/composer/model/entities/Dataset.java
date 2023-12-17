package com.kpi.composer.model.entities;

import com.kpi.composer.model.SupportedFormats;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;

import java.util.List;
import java.util.Set;

@Entity
public class Dataset extends FileEntity {

    public static final Set<SupportedFormats> SUPPORTED_FORMATS = Set.of(SupportedFormats.JSON);

    @OneToMany(mappedBy = "dataset", fetch = FetchType.LAZY)
    private List<ComposedDocument> documents;

    @PreRemove
    private void setNullOnDelete() {
        documents.forEach(doc -> doc.setDataset(null));
    }
}
