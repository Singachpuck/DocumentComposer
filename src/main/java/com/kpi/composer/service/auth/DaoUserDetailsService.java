package com.kpi.composer.service.auth;

import com.kpi.composer.dao.UserDao;
import com.kpi.composer.model.entities.User;
import com.kpi.composer.service.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class DaoUserDetailsService implements UserDetailsService {

    private UserDao userDao;

    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<User> user = userDao.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User " + username + "does not exist");
        }
        return userMapper.entityToPrincipal(user.get());
    }
}
