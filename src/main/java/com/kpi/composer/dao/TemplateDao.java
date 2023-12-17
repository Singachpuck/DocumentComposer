package com.kpi.composer.dao;

import com.kpi.composer.model.entities.Template;

import java.util.Collection;
import java.util.Optional;

public interface TemplateDao {

    Collection<Template> findAll();

    Optional<Template> findById(Long id);

    Collection<Template> findAllByOwner(String username);

    Template save(Template template);

    long count(String owner);

    void deleteById(Long id);
}
