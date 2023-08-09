package com.kpi.composer.service;

import com.kpi.composer.dao.UserDao;
import com.kpi.composer.model.dto.UserDto;
import com.kpi.composer.model.entities.User;
import com.kpi.composer.service.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@AllArgsConstructor
public class UserService {

    private UserDao userDao;

    private UserMapper userMapper;

    private PasswordEncoder passwordEncoder;

    public User create(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userDao.save(userMapper.dtoToUser(userDto));
    }

    public Collection<User> findAll() {
        return userDao.findAll();
    }
}
