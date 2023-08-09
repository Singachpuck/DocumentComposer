package com.kpi.composer.service;

import com.kpi.composer.dao.TemplateDao;
import com.kpi.composer.model.dto.TemplateDto;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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

    public Template create(TemplateDto templateDto) {
        return templateDao.save(fileMapper.dtoToTemplate(templateDto));
    }

    public Template findById(Long templateId) {
        return templateDao.findById(templateId).get();
    }

    public TemplateDto findDtoById(Long templateId) {
        return fileMapper.templateToDto(this.findById(templateId));
    }
}
