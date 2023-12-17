package com.kpi.composer.controller.rest;

import com.kpi.composer.exception.NotOwnerException;
import com.kpi.composer.model.dto.DatasetDto;
import com.kpi.composer.service.DatasetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/datasets")
@RequiredArgsConstructor
@Validated
public class DatasetRestController {

    private final DatasetService datasetService;

    @GetMapping
    ResponseEntity<?> getAll() {
        // This operation is not allowed for DEFAULT privileged users.
        // Will be available in future when authorities will be extended.
        throw new NotOwnerException("Not enough privileges.");
    }

    @GetMapping("/{datasetId}")
    ResponseEntity<?> get(@PathVariable long datasetId) {
        return ResponseEntity.ok(datasetService.findDtoById(datasetId));
    }

    @GetMapping("/user/{username}")
    ResponseEntity<?> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(datasetService.findByOwner(username));
    }

    @PostMapping
    ResponseEntity<?> create(@Valid @RequestBody DatasetDto datasetDto) {
        DatasetDto dataset = datasetService.create(datasetDto);

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{datasetId}")
                .buildAndExpand(dataset.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(dataset);
    }

    @DeleteMapping("/{datasetId}")
    ResponseEntity<?> delete(@PathVariable long datasetId) {
        datasetService.delete(datasetId);

        return ResponseEntity
                .noContent()
                .build();
    }
}
