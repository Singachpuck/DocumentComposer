package com.kpi.composer.dao;

import com.kpi.composer.model.entities.ComposedDocument;

import java.util.Collection;
import java.util.Optional;

public interface ComposedDocumentDao {

    Collection<ComposedDocument> findAll();

    Optional<ComposedDocument> findById(Long id);

    Collection<ComposedDocument> findAllByOwner(String username);

    ComposedDocument save(ComposedDocument dataset);

    long count(String owner);


    void deleteLast(String owner);
}
