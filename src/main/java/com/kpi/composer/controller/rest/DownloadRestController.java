package com.kpi.composer.controller.rest;

import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.ComposeService;
import com.kpi.composer.service.DatasetService;
import com.kpi.composer.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/download")
@RequiredArgsConstructor
public class DownloadRestController {

    private final ComposeService composeService;

    private final TemplateService templateService;

    private final DatasetService datasetService;

    @GetMapping("/compose/{composeId}")
    ResponseEntity<?> downloadComposed(@PathVariable long composeId) {
        final ComposedDocument toDownload = composeService.findById(composeId);

        return ResponseEntity.ok()
                .contentType(toDownload.getFormat().getContentType())
                .contentLength(toDownload.getSize())
                .body(toDownload.getBytes());
    }

    @GetMapping("/datasets/{datasetId}")
    ResponseEntity<?> downloadDataset(@PathVariable long datasetId) {
        final Dataset toDownload = datasetService.findById(datasetId);

        return ResponseEntity.ok()
                .contentType(toDownload.getFormat().getContentType())
                .contentLength(toDownload.getSize())
                .body(toDownload.getBytes());
    }

    @GetMapping("/templates/{templateId}")
    ResponseEntity<?> downloadTemplate(@PathVariable long templateId) {
        final Template toDownload = templateService.findById(templateId);

        return ResponseEntity.ok()
                .contentType(toDownload.getFormat().getContentType())
                .contentLength(toDownload.getSize())
                .body(toDownload.getBytes());
    }
}
