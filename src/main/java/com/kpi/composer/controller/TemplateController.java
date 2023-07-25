package com.kpi.composer.controller;

import com.kpi.composer.dto.TemplateDto;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.TemplateService;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
@RequestMapping("/templates")
@AllArgsConstructor
public class TemplateController {

    private TemplateService templateService;

    @GetMapping
    String templatesPage(Model model) {
        model.addAttribute("templates", templateService.findAll());
        return "templates";
    }

    @GetMapping("/{templateId}")
    String templatePage(@PathVariable int templateId) {
        return "template";
    }
}
