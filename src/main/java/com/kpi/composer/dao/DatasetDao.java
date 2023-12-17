package com.kpi.composer.dao;

import com.kpi.composer.model.entities.Dataset;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Transactional
public interface DatasetDao {

    Collection<Dataset> findAll();

    Optional<Dataset> findById(Long id);

    Collection<Dataset> findAllByOwner(String username);

    Dataset save(Dataset dataset);

    long count(String owner);

    void deleteById(Long id);
}
