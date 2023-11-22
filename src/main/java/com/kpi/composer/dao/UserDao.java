package com.kpi.composer.dao;

import com.kpi.composer.model.entities.User;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    Optional<User> findById(Long id);

    Collection<User> findAll();

    Optional<User> findByUsername(String username);

    User save(User user);

    boolean existsByUsername(String username);

    void deleteByUsername(String username);
}
