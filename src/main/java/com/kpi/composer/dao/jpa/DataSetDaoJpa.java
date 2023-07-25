package com.kpi.composer.dao.jpa;

import com.kpi.composer.dao.DatasetDao;
import com.kpi.composer.model.entities.Dataset;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSetDaoJpa extends DatasetDao, CrudRepository<Dataset, Long> {
}
