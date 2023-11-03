package com.kpi.composer.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {

    private String username;

    private String authorities;

    private String token;

    private int expiresIn;
}
