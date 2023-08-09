package com.kpi.composer.dao.jpa;

import com.kpi.composer.dao.DatasetDao;
import com.kpi.composer.model.entities.Dataset;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface DataSetDaoJpa extends DatasetDao, CrudRepository<Dataset, Long> {
}
