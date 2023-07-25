package com.kpi.composer.dao.jpa;

import com.kpi.composer.dao.TemplateDao;
import com.kpi.composer.model.entities.Template;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateDaoJpa extends TemplateDao,  CrudRepository<Template, Long> {
}
