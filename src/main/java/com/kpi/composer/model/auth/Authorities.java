package com.kpi.composer.model.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public enum Authorities {
    DEFAULT("DEFAULT");

    private final GrantedAuthority authority;

    Authorities(String authority) {
        this.authority = new SimpleGrantedAuthority(authority);
    }

    public GrantedAuthority getAuthority() {
        return authority;
    }

    public static List<GrantedAuthority> getAllAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (Authorities a : Authorities.values()) {
            authorities.add(a.getAuthority());
        }
        return authorities;
    }
}
