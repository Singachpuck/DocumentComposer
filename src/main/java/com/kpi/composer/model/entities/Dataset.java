package com.kpi.composer.model.entities;

import com.kpi.composer.model.SupportedFormats;
import jakarta.persistence.Entity;

import java.util.Set;

@Entity
public class Dataset extends FileEntity {

    public static final String TARGET_NAME = "DATASET";

    public static final Set<SupportedFormats> SUPPORTED_FORMATS = Set.of(SupportedFormats.JSON);
}
