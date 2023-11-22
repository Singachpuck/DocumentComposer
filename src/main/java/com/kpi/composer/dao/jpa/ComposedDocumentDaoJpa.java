package com.kpi.composer.dao.jpa;

import com.kpi.composer.dao.ComposedDocumentDao;
import com.kpi.composer.model.entities.ComposedDocument;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
@Transactional
public interface ComposedDocumentDaoJpa extends ComposedDocumentDao, CrudRepository<ComposedDocument, Long> {

    @Query("select cd from ComposedDocument cd where cd.owner.username = ?1")
    Collection<ComposedDocument> findAllByOwner(String username);

    @Query("select count(*) from ComposedDocument cd where cd.owner.username = ?1")
    long count(String owner);

    @Override
    @Modifying
    @Query("delete from ComposedDocument where id=(select id from ComposedDocument cd where cd.owner.username = ?1 order by created desc limit 1)")
    void deleteLast(String owner);
}
