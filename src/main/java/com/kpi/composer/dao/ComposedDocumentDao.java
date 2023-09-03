package com.kpi.composer.dao;

import com.kpi.composer.model.entities.ComposedDocument;

import java.util.Collection;
import java.util.Optional;

public interface ComposedDocumentDao {

    Collection<ComposedDocument> findAll();

    Optional<ComposedDocument> findById(Long id);

    ComposedDocument save(ComposedDocument dataset);

    long count();

    void deleteLast();
}
