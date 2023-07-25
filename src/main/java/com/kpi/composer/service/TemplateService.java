package com.kpi.composer.service;

import com.kpi.composer.dao.TemplateDao;
import com.kpi.composer.dto.FileDto;
import com.kpi.composer.dto.TemplateDto;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@Service
@AllArgsConstructor
public class TemplateService {

    private TemplateDao templateDao;

    private FileMapper fileMapper;

    public Collection<TemplateDto> findAll() {
        return templateDao
                .findAll()
                .stream()
                .map(fileMapper::templateToDto)
                .toList();
    }

    public Template create(FileDto templateDto, MultipartFile file) {
        return templateDao.save(fileMapper.dtoToTemplate(templateDto, file));
    }

    public Template findById(long templateId) {
        return templateDao.findById(templateId).get();
    }

    public TemplateDto findDtoById(long templateId) {
        return fileMapper.templateToDto(this.findById(templateId));
    }
}
