package com.kpi.composer.service.mapper;

import com.kpi.composer.dto.UserDto;
import com.kpi.composer.model.auth.Authorities;
import com.kpi.composer.model.auth.UserPrincipal;
import com.kpi.composer.model.entities.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring",
        imports = Authorities.class
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User dtoToUser(UserDto userDto);

    @Mapping(target = "authorities", expression = "java(Authorities.getAllAuthorities())")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "enabled", constant = "true")
    UserPrincipal entityToPrincipal(User user);
}
