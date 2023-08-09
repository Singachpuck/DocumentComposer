package com.kpi.composer.controller.rest;

import com.kpi.composer.model.dto.ComposedDocumentDto;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.service.ComposeService;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/compose")
@RequiredArgsConstructor
public class ComposedDocumentRestController {

    private final ComposeService composeService;

    private final FileMapper fileMapper;

    @GetMapping
    ResponseEntity<?> getAll() {
        return ResponseEntity.ok(composeService.findAll());
    }

    @GetMapping("/{composeId}")
    ResponseEntity<?> get(@PathVariable long composeId) {
        return ResponseEntity.ok(composeService.findById(composeId));
    }

    @PostMapping
    ResponseEntity<?> compose(@RequestBody ComposedDocumentDto documentDto) {
        final ComposedDocument composedDocument = composeService.composeAndSave(documentDto.getTemplateId(),
                documentDto.getDatasetId());

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{composeId}")
                .buildAndExpand(composedDocument.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(fileMapper.composedToDto(composedDocument));
    }
}
