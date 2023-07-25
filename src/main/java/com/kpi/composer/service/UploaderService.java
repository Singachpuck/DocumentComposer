package com.kpi.composer.service;

import com.kpi.composer.dto.FileDto;
import com.kpi.composer.exception.UnsupportedFormatException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class UploaderService {

    private List<Uploader> uploaders;

    public String tryUpload(String target, FileDto fileDto, MultipartFile file) {
        try {
            for (Uploader uploader : uploaders) {
                if (uploader.supports(target, fileDto)) {
                    return uploader.upload(target, fileDto, file);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
        throw new UnsupportedFormatException("Unrecognized format: " + fileDto.getFormat().name() + " for target: " + target);
    }
}
