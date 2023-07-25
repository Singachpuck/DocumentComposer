package com.kpi.composer.dao;

import com.kpi.composer.model.entities.Dataset;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public interface DatasetDao {

    Collection<Dataset> findAll();

    Optional<Dataset> findById(Long id);

    Dataset save(Dataset dataset);
}
