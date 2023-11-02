package com.kpi.composer.controller.rest;

import com.kpi.composer.model.dto.UserDto;
import com.kpi.composer.model.entities.User;
import com.kpi.composer.service.UserService;
import com.kpi.composer.service.security.jwt.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;

    private final TokenService tokenService;

    @PostMapping("/auth/token")
    ResponseEntity<?> token() {
        return ResponseEntity.ok(tokenService.generateToken());
    }

    @GetMapping("/users")
    ResponseEntity<?> get() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/users/{username}")
    ResponseEntity<?> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findDtoByUsername(username));
    }

    @PostMapping("/users")
    ResponseEntity<?> register(@Valid @RequestBody UserDto user) {
        final User created = userService.create(user);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{username}")
                .buildAndExpand(created.getUsername())
                .toUri();

        return ResponseEntity
                .created(uri)
                .build();
    }
}
