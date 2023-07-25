package com.kpi.composer.dao;

import com.kpi.composer.model.entities.Template;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public interface TemplateDao {

    Collection<Template> findAll();

    Optional<Template> findById(long id);

    Template save(Template template);
}
