package com.kpi.composer.service;

import com.kpi.composer.dao.ComposedDocumentDao;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.exception.NotOwnerException;
import com.kpi.composer.model.dto.ComposedDocumentDto;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.ComposeFactory;
import com.kpi.composer.service.mapper.FileMapper;
import com.kpi.composer.service.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComposeService {

    private final TemplateService templateService;

    private final DatasetService datasetService;

    private final ComposedDocumentDao composedDocumentDao;

    private final UserService userService;

    private final ComposeFactory composeFactory;

    private final FileMapper fileMapper;

    private final AuthenticationFacade auth;

    @Value("${entity.composed.limit}")
    private long documentLimit;

    public Collection<ComposedDocumentDto> findAll() {
        return composedDocumentDao
                .findAll()
                .stream()
                .map(fileMapper::composedToDto)
                .toList();
    }

    public ComposedDocument findById(Long id) {
        final Optional<ComposedDocument> document = composedDocumentDao.findById(id);
        if (document.isEmpty()) {
            throw new EntityException("Document with id " + id + " does not exist.");
        }
        final String currentUsername = this.getCurrentUsername();
        if (!document.get().getOwner().getUsername().equals(currentUsername)) {
            throw new NotOwnerException("User " + currentUsername + " can not perform this operation.");
        }
        return document.get();
    }

    public ComposedDocumentDto findDtoById(Long id) {
        return fileMapper.composedToDto(this.findById(id));
    }

    public Collection<ComposedDocumentDto> findByOwner(String username) {
        final String currentUsername = this.getCurrentUsername();
        if (!username.equals(currentUsername)) {
            throw new NotOwnerException("User " + currentUsername + " can not perform this operation.");
        }
        return composedDocumentDao
                .findAllByOwner(username)
                .stream()
                .map(fileMapper::composedToDto)
                .toList();
    }

    @Transactional
    public ComposedDocumentDto composeAndSave(long templateId, long datasetId) {
        final Template template = templateService.findById(templateId);
        final Dataset dataset = datasetService.findById(datasetId);

        final String currentUsername = this.getCurrentUsername();
        if (!template.getOwner().getUsername().equals(currentUsername)) {
            throw new NotOwnerException("User " + currentUsername + " can not perform this operation.");
        }
        if (!dataset.getOwner().getUsername().equals(currentUsername)) {
            throw new NotOwnerException("User " + currentUsername + " can not perform this operation.");
        }

        final ComposedDocument composedDocument = composeFactory
                .getComposer(template)
                .compose(dataset, template);
        composedDocument.setOwner(userService.findByUsername(currentUsername));

        if (composedDocumentDao.count(currentUsername) >= documentLimit) {
            composedDocumentDao.deleteLast(currentUsername);
        }
        return fileMapper.composedToDto(composedDocumentDao.save(composedDocument));
    }

    private String getCurrentUsername() {
        return auth.getAuthentication().getName();
    }
}
