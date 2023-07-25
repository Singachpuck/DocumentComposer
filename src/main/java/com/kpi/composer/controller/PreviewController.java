package com.kpi.composer.controller;

import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.DatasetService;
import com.kpi.composer.service.TemplateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view")
@AllArgsConstructor
public class PreviewController {

    private TemplateService templateService;

    private DatasetService datasetService;

    @GetMapping("/templates/{templateId}")
    ResponseEntity<?> previewTemplate(@PathVariable long templateId) {
        final Template template = templateService.findById(templateId);
        return ResponseEntity
                .ok()
                .contentType(template.getFormat().getContentType())
                .contentLength(template.getBytes().length)
                .body(template.getBytes());
    }

    @GetMapping("/datasets/{datasetId}")
    ResponseEntity<?> previewDataset(@PathVariable long datasetId) {
        final Dataset dataset = datasetService.findById(datasetId);
        return ResponseEntity
                .ok()
                .contentType(dataset.getFormat().getContentType())
                .contentLength(dataset.getBytes().length)
                .body(dataset.getBytes());
    }
}
