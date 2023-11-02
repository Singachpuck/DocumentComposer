package com.kpi.composer.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ComposedDocument extends FileEntity {

    @ManyToOne
    @JoinColumn(name = "template_id", referencedColumnName = "id")
    private Template template;

    @ManyToOne
    @JoinColumn(name = "dataset_id", referencedColumnName = "id")
    private Dataset dataset;
}
