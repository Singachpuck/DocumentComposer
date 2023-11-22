package com.kpi.composer.dao.jpa;

import com.kpi.composer.dao.TemplateDao;
import com.kpi.composer.model.entities.Template;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
@Transactional
public interface TemplateDaoJpa extends TemplateDao, CrudRepository<Template, Long> {

    @Query(value = "select t from Template t where t.owner.username = ?1")
    Collection<Template> findAllByOwner(String username);

    @Query("select count(*) from Template t where t.owner.username = ?1")
    long count(String username);
}
