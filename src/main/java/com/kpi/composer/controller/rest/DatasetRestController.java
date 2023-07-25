package com.kpi.composer.controller.rest;

import com.kpi.composer.service.DatasetService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/datasets")
@AllArgsConstructor
public class DatasetRestController {

    private DatasetService datasetService;

    @GetMapping
    ResponseEntity<?> getAll() {
        return ResponseEntity.ok(datasetService.findAll());
    }
}
