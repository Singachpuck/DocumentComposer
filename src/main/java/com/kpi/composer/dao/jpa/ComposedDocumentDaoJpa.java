package com.kpi.composer.dao.jpa;

import com.kpi.composer.dao.ComposedDocumentDao;
import com.kpi.composer.model.entities.ComposedDocument;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ComposedDocumentDaoJpa extends ComposedDocumentDao, CrudRepository<ComposedDocument, Long> {

    @Override
    @Modifying
    @Query("delete from ComposedDocument where id=(select id from ComposedDocument order by created desc limit 1)")
    void deleteLast();
}
