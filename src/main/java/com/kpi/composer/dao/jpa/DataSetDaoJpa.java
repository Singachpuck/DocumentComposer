package com.kpi.composer.dao.jpa;

import com.kpi.composer.dao.DatasetDao;
import com.kpi.composer.model.entities.Dataset;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Repository
@Transactional
public interface DataSetDaoJpa extends DatasetDao, CrudRepository<Dataset, Long> {

    @Query(value = "select d from Dataset d where d.owner.username = ?1")
    Collection<Dataset> findAllByOwner(String username);

    @Query("select count(*) from Dataset d where d.owner.username = ?1")
    long count(String username);
}
