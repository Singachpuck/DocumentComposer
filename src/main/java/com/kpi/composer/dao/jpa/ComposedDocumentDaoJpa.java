package com.kpi.composer.dao.jpa;

import com.kpi.composer.dao.ComposedDocumentDao;
import com.kpi.composer.model.entities.ComposedDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ComposedDocumentDaoJpa extends ComposedDocumentDao, CrudRepository<ComposedDocument, Long> {
}
