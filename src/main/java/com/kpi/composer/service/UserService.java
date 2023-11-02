package com.kpi.composer.service;

import com.kpi.composer.dao.UserDao;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.model.dto.UserDto;
import com.kpi.composer.model.entities.User;
import com.kpi.composer.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public User create(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userDao.save(userMapper.dtoToUser(userDto));
    }

    public Collection<User> findAll() {
        return userDao.findAll();
    }

    public User findByUsername(String username) {
        final Optional<User> user = userDao.findByUsername(username);
        if (user.isEmpty()) {
            throw new EntityException("User with username \"" + username + "\" does not exist.");
        }
        return user.get();
    }

    public UserDto findDtoByUsername(String username) {
        return userMapper.userToDto(this.findByUsername(username));
    }
}
