package com.kpi.composer.controller.rest;

import com.kpi.composer.exception.NotOwnerException;
import com.kpi.composer.model.dto.ComposedDocumentDto;
import com.kpi.composer.service.ComposeService;
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

    @GetMapping
    ResponseEntity<?> getAll() {
        // This operation is not allowed for DEFAULT privileged users.
        // Will be available in future when authorities will be extended.
        throw new NotOwnerException("Not enough privileges.");
    }

    @GetMapping("/{composeId}")
    ResponseEntity<?> get(@PathVariable long composeId) {
        return ResponseEntity.ok(composeService.findDtoById(composeId));
    }

    @GetMapping("/user/{username}")
    ResponseEntity<?> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(composeService.findByOwner(username));
    }

    @PostMapping
    ResponseEntity<?> compose(@RequestBody ComposedDocumentDto documentDto) {
        final ComposedDocumentDto composedDocument = composeService.composeAndSave(documentDto.getTemplateId(),
                documentDto.getDatasetId());

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{composeId}")
                .buildAndExpand(composedDocument.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(composedDocument);
    }

    @DeleteMapping("/{composeId}")
    ResponseEntity<?> delete(@PathVariable long composeId) {
        composeService.delete(composeId);

        return ResponseEntity
                .noContent()
                .build();
    }
}
