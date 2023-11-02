package com.kpi.composer.service.mapper;

import com.kpi.composer.model.auth.Authorities;
import com.kpi.composer.model.dto.UserDto;
import com.kpi.composer.model.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        imports = Authorities.class
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User dtoToUser(UserDto userDto);

    UserDto userToDto(User user);

    default org.springframework.security.core.userdetails.User entityToUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                Authorities.getAllAuthorities());
    }
}
