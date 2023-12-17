package com.kpi.composer.dao;

import com.kpi.composer.model.entities.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Transactional
public interface UserDao {

    Optional<User> findById(Long id);

    Collection<User> findAll();

    Optional<User> findByUsername(String username);

    User save(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void deleteByUsername(String username);

    long count();
}
