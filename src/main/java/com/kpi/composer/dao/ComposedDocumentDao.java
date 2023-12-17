package com.kpi.composer.dao;

import com.kpi.composer.model.entities.ComposedDocument;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Transactional
public interface ComposedDocumentDao {

    Collection<ComposedDocument> findAll();

    Optional<ComposedDocument> findById(Long id);

    Collection<ComposedDocument> findAllByOwner(String username);

    ComposedDocument save(ComposedDocument dataset);

    long count(String owner);

    void deleteLast(String owner);

    void deleteById(Long id);
}
