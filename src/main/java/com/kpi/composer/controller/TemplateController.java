package com.kpi.composer.controller;

import com.kpi.composer.service.TemplateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
