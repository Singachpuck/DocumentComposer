package com.kpi.composer.config;

import com.kpi.composer.model.auth.Authorities;
import com.kpi.composer.service.security.DaoUserDetailsService;
import com.kpi.composer.service.security.ExceptionHandlerFilter;
import com.kpi.composer.service.security.jwt.JWTValidationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors() // TODO: implement
                .disable()
                .csrf() // TODO: implement
                .disable()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .addFilterBefore(new JWTValidationFilter(), BasicAuthenticationFilter.class)
                .addFilterBefore(new ExceptionHandlerFilter(), JWTValidationFilter.class)
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers("/api/v1/compose/**",
                                    "/api/v1/datasets/**",
                                    "/api/v1/download/**",
                                    "/api/v1/templates/**",
                                    "/api/v1/users/**",
                                    "/api/v1/auth/token"
                            )
                            .hasAuthority(Authorities.DEFAULT.getAuthority().getAuthority())
                            .requestMatchers(HttpMethod.POST, "/api/v1/users")
                            .permitAll();
                })
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    @DependsOn("passwordEncoder")
    DaoAuthenticationProvider authenticationProvider(DaoUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        final DaoAuthenticationProvider ap = new DaoAuthenticationProvider();
        ap.setUserDetailsService(userDetailsService);
        ap.setPasswordEncoder(passwordEncoder);
        return ap;
    }

    @Bean
    WebSecurityCustomizer customizer() {
        return web -> web.ignoring().requestMatchers("/css/**", "/js/**");
    }
}
