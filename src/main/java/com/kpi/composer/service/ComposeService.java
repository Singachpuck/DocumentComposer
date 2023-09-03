package com.kpi.composer.service;

import com.kpi.composer.dao.ComposedDocumentDao;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.model.dto.ComposedDocumentDto;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.ComposeFactory;
import com.kpi.composer.service.mapper.FileMapper;
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

    private final ComposeFactory composeFactory;

    private final FileMapper fileMapper;

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
        return document.get();
    }

    public ComposedDocumentDto findDtoById(Long id) {
        return fileMapper.composedToDto(this.findById(id));
    }

    @Transactional
    public ComposedDocumentDto composeAndSave(long templateId, long datasetId) {
        final Template template = templateService.findById(templateId);
        final Dataset dataset = datasetService.findById(datasetId);

        final ComposedDocument composedDocument = composeFactory
                .getComposer(template)
                .compose(dataset, template);
        if (composedDocumentDao.count() >= documentLimit) {
            composedDocumentDao.deleteLast();
        }
        return fileMapper.composedToDto(composedDocumentDao.save(composedDocument));
    }
}
