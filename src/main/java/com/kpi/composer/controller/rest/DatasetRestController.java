package com.kpi.composer.controller.rest;

import com.kpi.composer.model.dto.DatasetDto;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.service.DatasetService;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/datasets")
@RequiredArgsConstructor
public class DatasetRestController {

    private final DatasetService datasetService;

    private final FileMapper fileMapper;

    @GetMapping
    ResponseEntity<?> getAll() {
        return ResponseEntity.ok(datasetService.findAll());
    }

    @PostMapping
    ResponseEntity<?> create(@RequestBody DatasetDto datasetDto) {
        Dataset dataset = datasetService.create(datasetDto);

        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{datasetId}")
                .buildAndExpand(dataset.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(fileMapper.datasetToDto(dataset));
    }
}
