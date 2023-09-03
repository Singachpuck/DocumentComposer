package com.kpi.composer.service;

import com.kpi.composer.dao.TemplateDao;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.exception.MaxNumberExceededException;
import com.kpi.composer.model.dto.TemplateDto;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateDao templateDao;

    private final FileMapper fileMapper;

    @Value("${entity.template.max-number}")
    private long templateMaxNumber;

    public Collection<TemplateDto> findAll() {
        return templateDao
                .findAll()
                .stream()
                .map(fileMapper::templateToDto)
                .toList();
    }

    public TemplateDto create(TemplateDto templateDto) {
        if (templateDao.count() >= templateMaxNumber) {
            throw new MaxNumberExceededException("Max number of templates reached: " + templateMaxNumber);
        }
        return fileMapper.templateToDto(templateDao.save(fileMapper.dtoToTemplate(templateDto)));
    }

    public Template findById(Long id) {
        final Optional<Template> template = templateDao.findById(id);
        if (template.isEmpty()) {
            throw new EntityException("Template with id " + id + " does not exist.");
        }
        return template.get();
    }

    public TemplateDto findDtoById(Long templateId) {
        return fileMapper.templateToDto(this.findById(templateId));
    }
}
