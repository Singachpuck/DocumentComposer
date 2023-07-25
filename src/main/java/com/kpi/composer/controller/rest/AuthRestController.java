package com.kpi.composer.controller.rest;

import com.kpi.composer.dto.UserDto;
import com.kpi.composer.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class AuthRestController {

    private UserService userService;

    @GetMapping
    ResponseEntity<?> get() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping
    ResponseEntity<?> register(@Valid @RequestBody UserDto user) {
        userService.create(user);

        final URI uri = ServletUriComponentsBuilder
                .fromUriString("/profile")
                .build()
                .toUri();

        return ResponseEntity
                .created(uri)
                .build();
    }
}
