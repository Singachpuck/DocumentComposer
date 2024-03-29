package com.kpi.composer.dao.jpa;

import com.kpi.composer.dao.UserDao;
import com.kpi.composer.model.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDaoJpa extends UserDao, CrudRepository<User, Long> {
}
