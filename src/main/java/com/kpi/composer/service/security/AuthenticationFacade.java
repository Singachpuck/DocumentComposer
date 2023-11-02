package com.kpi.composer.service.security;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacade {
    Authentication getAuthentication();
}
