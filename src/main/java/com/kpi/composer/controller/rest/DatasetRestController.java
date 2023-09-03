package com.kpi.composer.controller.rest;

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
        return ResponseEntity.ok(datasetService.findAll());
    }

    @GetMapping("/{datasetId}")
    ResponseEntity<?> get(@PathVariable long datasetId) {
        return ResponseEntity.ok(datasetService.findDtoById(datasetId));
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
}
