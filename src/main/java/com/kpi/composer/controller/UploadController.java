package com.kpi.composer.controller;

import com.kpi.composer.service.UploaderService;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/upload")
@AllArgsConstructor
public class UploadController {

    private FileMapper fileMapper;

    private UploaderService uploaderService;


//    @GetMapping
//    String upload(@RequestParam MultipartFile file,
//                             @RequestParam String target,
//                             @ModelAttribute("fileDto") FileDto fileDto) {
//        final String redirectString = uploaderService.tryUpload(target, fileDto, file);
//        return "redirect:" + redirectString;
//    }
//
//    @ModelAttribute("fileDto")
//    FileDto getFileDto(@RequestParam String name,
//                           @RequestParam SupportedFormats format) {
//        return fileMapper.paramsToDto(name, format);
//    }
}
