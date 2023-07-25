package com.kpi.composer.controller;

import com.kpi.composer.dto.FileDto;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.FileEntity;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.DatasetService;
import com.kpi.composer.service.TemplateService;
import com.kpi.composer.service.Uploader;
import com.kpi.composer.service.UploaderService;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/upload")
@AllArgsConstructor
public class UploadController {

    private FileMapper fileMapper;

    private UploaderService uploaderService;


    @GetMapping
    String upload(@RequestParam MultipartFile file,
                             @RequestParam String target,
                             @ModelAttribute("fileDto") FileDto fileDto) {
        final String redirectString = uploaderService.tryUpload(target, fileDto, file);
        return "redirect:" + redirectString;
    }

    @ModelAttribute("fileDto")
    FileDto getFileDto(@RequestParam String name,
                           @RequestParam SupportedFormats format) {
        return fileMapper.paramsToDto(name, format);
    }
}
