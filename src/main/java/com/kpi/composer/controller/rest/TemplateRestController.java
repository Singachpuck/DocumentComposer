package com.kpi.composer.controller.rest;

import com.kpi.composer.dto.TemplateDto;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.TemplateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/templates")
@AllArgsConstructor
public class TemplateRestController {

    private TemplateService templateService;

    @GetMapping
    ResponseEntity<?> getAll() {
        return ResponseEntity.ok(templateService.findAll());
    }

    @GetMapping("/{templateId}")
    ResponseEntity<?> get(@PathVariable long templateId) {
        return ResponseEntity.ok(templateService.findDtoById(templateId));
    }
}
