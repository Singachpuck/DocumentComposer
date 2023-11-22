package com.kpi.composer.dao.jpa;

import com.kpi.composer.dao.UserDao;
import com.kpi.composer.model.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface UserDaoJpa extends UserDao, CrudRepository<User, Long> {

    @Override
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    void deleteByUsername(String username);
}
