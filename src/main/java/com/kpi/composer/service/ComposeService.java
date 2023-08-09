package com.kpi.composer.service;

import com.kpi.composer.dao.ComposedDocumentDao;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.ComposeFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ComposeService {

    private final TemplateService templateService;

    private final DatasetService datasetService;

    private final ComposedDocumentDao composedDocumentDao;

    private final ComposeFactory composeFactory;

    public Collection<ComposedDocument> findAll() {
        return composedDocumentDao.findAll();
    }

    public ComposedDocument findById(Long id) {
        return composedDocumentDao.findById(id).get();
    }

    public ComposedDocument composeAndSave(long templateId, long datasetId) {
        final Template template = templateService.findById(templateId);
        final Dataset dataset = datasetService.findById(datasetId);

        final ComposedDocument composedDocument = composeFactory
                .getComposer(template)
                .compose(dataset, template);
        return composedDocumentDao.save(composedDocument);
    }
}
