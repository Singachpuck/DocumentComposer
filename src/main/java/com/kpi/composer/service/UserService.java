package com.kpi.composer.service;

import com.kpi.composer.dao.UserDao;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.exception.NotOwnerException;
import com.kpi.composer.model.dto.UserDto;
import com.kpi.composer.model.entities.User;
import com.kpi.composer.service.mapper.UserMapper;
import com.kpi.composer.service.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationFacade auth;

    public UserDto create(UserDto userDto) {
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return userMapper.userToDto(userDao.save(userMapper.dtoToUser(userDto)));
    }

    public UserDto update(String username, UserDto userDto, BindingResult result) {
        final String currentUsername = this.getCurrentUsername();
        if (!username.equals(currentUsername)) {
            throw new NotOwnerException("User " + currentUsername + " can not perform this operation.");
        }
        final User user = this.findByUsername(username);
        if (userDto.getUsername() != null) {
            if (result.hasFieldErrors("username")) {
                throw new EntityException(this.findErrorMessage("username", result));
            }
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getEmail() != null) {
            if (result.hasFieldErrors("email")) {
                throw new EntityException(this.findErrorMessage("email", result));
            }
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            if (result.hasFieldErrors("password")) {
                throw new EntityException(this.findErrorMessage("password", result));
            }
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        return userMapper.userToDto(userDao.save(user));
    }

    private String findErrorMessage(String fieldName, BindingResult result) {
        return Optional.ofNullable(result.getFieldError(fieldName))
                .map(FieldError::getDefaultMessage)
                .orElse("Can't update " + fieldName + ". Value '" + result.getFieldValue(fieldName) + "' is not valid.");
    }

    public void delete(String username) {
        if (!userDao.existsByUsername(username)) {
            throw new EntityException("User with username \"" + username + "\" does not exist.");
        }
        final String currentUsername = this.getCurrentUsername();
        if (!username.equals(currentUsername)) {
            throw new NotOwnerException("User " + currentUsername + " can not perform this operation.");
        }
        userDao.deleteByUsername(username);
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

    private String getCurrentUsername() {
        return auth.getAuthentication().getName();
    }
}
