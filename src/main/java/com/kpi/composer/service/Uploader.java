package com.kpi.composer.service;

import com.kpi.composer.exception.UnsupportedFormatException;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.dto.FileDto;
import com.kpi.composer.model.entities.FileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class Uploader {

    private final Class<? extends FileEntity> uploadTarget;

    private final BiFunction<FileDto, MultipartFile, FileEntity> uploadFunction;

    public String upload(String target, FileDto fileDto, MultipartFile file) throws NoSuchFieldException, IllegalAccessException {
        final Field redirectUriField = uploadTarget.getDeclaredField("REDIRECT_URI");
        final String redirectUri = (String) redirectUriField.get(redirectUriField);

        if (this.supports(target, fileDto)) {
            final FileEntity fileEntity = this.uploadFunction.apply(fileDto, file);
            return ServletUriComponentsBuilder
                    .fromUriString(redirectUri)
                    .buildAndExpand(fileEntity.getId())
                    .toUriString();
        }
        throw new UnsupportedFormatException("Unrecognized format: " + fileDto.getFormat().name() + " for target: " + target);
    }
    public boolean supports(String target, FileDto fileDto) throws NoSuchFieldException, IllegalAccessException {
        final Field targetField = uploadTarget.getDeclaredField("TARGET_NAME");
        final String t = (String) targetField.get(targetField);
        final Field supportedFormatsField = uploadTarget.getDeclaredField("SUPPORTED_FORMATS");
        final Set<SupportedFormats> supportedFormats = (Set<SupportedFormats>) supportedFormatsField.get(supportedFormatsField);
        return t.equals(target) && supportedFormats.contains(fileDto.getFormat());
    }
}
