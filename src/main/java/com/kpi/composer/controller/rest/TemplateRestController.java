package com.kpi.composer.controller.rest;

import com.kpi.composer.model.dto.TemplateDto;
import com.kpi.composer.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
@Validated
public class TemplateRestController {

    private final TemplateService templateService;

    @GetMapping
    ResponseEntity<?> getAll() {
        return ResponseEntity.ok(templateService.findAll());
    }

    @GetMapping("/{templateId}")
    ResponseEntity<?> get(@PathVariable long templateId) {
        return ResponseEntity.ok(templateService.findDtoById(templateId));
    }

    @PostMapping
    ResponseEntity<?> create(@Valid @RequestBody TemplateDto templateDto) {
        TemplateDto template = templateService.create(templateDto);

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{templateId}")
                .buildAndExpand(template.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(template);
    }
}
