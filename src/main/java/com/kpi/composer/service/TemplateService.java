package com.kpi.composer.service;

import com.kpi.composer.dao.TemplateDao;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.exception.MaxNumberExceededException;
import com.kpi.composer.exception.NotOwnerException;
import com.kpi.composer.model.dto.TemplateDto;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.model.entities.User;
import com.kpi.composer.service.mapper.FileMapper;
import com.kpi.composer.service.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateDao templateDao;

    private final UserService userService;

    private final FileMapper fileMapper;

    private final AuthenticationFacade auth;

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
        final String currentUsername = this.getCurrentUsername();
        if (templateDao.count(currentUsername) >= templateMaxNumber) {
            throw new MaxNumberExceededException("Max number of templates reached: " + templateMaxNumber);
        }

        final User owner = userService.findByUsername(currentUsername);
        return fileMapper.templateToDto(templateDao.save(fileMapper.dtoToTemplate(templateDto, owner)));
    }

    public Collection<TemplateDto> findByOwner(String username) {
        final String currentUsername = this.getCurrentUsername();
        if (!username.equals(currentUsername)) {
            throw new NotOwnerException("User " + currentUsername + " can not perform this operation.");
        }
        return templateDao
                .findAllByOwner(username)
                .stream()
                .map(fileMapper::templateToDto)
                .toList();
    }

    public Template findById(Long id) {
        final Optional<Template> template = templateDao.findById(id);
        if (template.isEmpty()) {
            throw new EntityException("Template with id " + id + " does not exist.");
        }
        final String currentUsername = this.getCurrentUsername();
        if (!template.get().getOwner().getUsername().equals(currentUsername)) {
            throw new NotOwnerException("User " + currentUsername + " can not perform this operation.");
        }
        return template.get();
    }

    public TemplateDto findDtoById(Long templateId) {
        return fileMapper.templateToDto(this.findById(templateId));
    }

    private String getCurrentUsername() {
        return auth.getAuthentication().getName();
    }
}
