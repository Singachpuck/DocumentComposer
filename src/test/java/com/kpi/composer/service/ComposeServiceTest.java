package com.kpi.composer.service;

import com.kpi.composer.TestUtil;
import com.kpi.composer.dao.ComposedDocumentDao;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.dto.ComposedDocumentDto;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.ComposeFactory;
import com.kpi.composer.service.compose.Composer;
import com.kpi.composer.service.mapper.FileMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComposeServiceTest {

    private static final List<ComposedDocument> fakeDocuments = TestUtil.documentExample();

    private static final ToDtoAnswer toDtoAnswer = new ToDtoAnswer();

    @InjectMocks
    private ComposeService service;

    @Mock
    private DatasetService datasetService;

    @Mock
    private TemplateService templateService;

    @Mock
    private ComposeFactory composeFactory;

    @Mock
    private ComposedDocumentDao dao;

    @Mock
    private Composer composer;

    @Mock
    private FileMapper mapper;

    @Test
    void findAllTest() {
        when(dao.findAll()).thenReturn(fakeDocuments);
        when(mapper.composedToDto(isA(ComposedDocument.class))).thenAnswer(toDtoAnswer);

        final Collection<ComposedDocumentDto> result = service.findAll();

        assertEquals(3, result.size());
        int i = 0;
        for (ComposedDocumentDto dto : result) {
            final ComposedDocument dataset = fakeDocuments.get(i++);
            this.verifyDto(dataset, dto);
        }

        verify(mapper, times(3)).composedToDto(any());
    }

    @Test
    void findByIdTest() {
        when(dao.findById(1L)).thenReturn(Optional.ofNullable(fakeDocuments.get(1)));
        final ComposedDocument document = service.findById(1L);
        assertEquals(document, fakeDocuments.get(1));
        verify(dao).findById(1L);
    }

    @Test
    void findDtoByIdTest() {
        when(dao.findById(1L)).thenReturn(Optional.ofNullable(fakeDocuments.get(1)));
        when(mapper.composedToDto(isA(ComposedDocument.class))).thenAnswer(toDtoAnswer);
        final ComposedDocumentDto dto = service.findDtoById(1L);
        verifyDto(fakeDocuments.get(1), dto);
        verify(dao).findById(1L);
    }

    @Test
    void notFoundTest() {
        when(dao.findById(1L)).thenThrow(EntityException.class);
        assertThrows(EntityException.class, () -> service.findDtoById(1L));
        assertThrows(EntityException.class, () -> service.findById(1L));

        verify(dao, times(2)).findById(1L);
    }

    @Test
    void noOverfillTest() {
        this.composeAndSave();
        verify(dao, never()).deleteLast();
    }

    @Test
    void overfillTest() {
        when(dao.count()).thenReturn(5L);
        this.composeAndSave();
        verify(dao, times(3)).deleteLast();
    }

    private void composeAndSave() {
        final List<Template> templates = TestUtil.templateExample();
        final List<Dataset> datasets = TestUtil.datasetExample();

        ReflectionTestUtils.setField(service, "documentLimit", 5L);
        when(templateService.findById(anyLong())).thenAnswer(i -> templates.get(i.getArgument(0, Long.class).intValue()));
        when(datasetService.findById(anyLong())).thenAnswer(i -> datasets.get(i.getArgument(0, Long.class).intValue()));
        when(composer.compose(any(), any())).thenReturn(fakeDocuments.get(0), fakeDocuments.get(1), fakeDocuments.get(2));
        when(composeFactory.getComposer(argThat(t -> t.getFormat().equals(SupportedFormats.DOCX)))).thenReturn(composer);
        when(mapper.composedToDto(isA(ComposedDocument.class))).thenAnswer(toDtoAnswer);
        for (int i = 0; i < 3; i++) {
            when(dao.save(fakeDocuments.get(i))).thenReturn(fakeDocuments.get(i));
        }

        ComposedDocumentDto result1 = service.composeAndSave(2, 0);
        verifyDto(fakeDocuments.get(0), result1);
        ComposedDocumentDto result2 = service.composeAndSave(0, 1);
        verifyDto(fakeDocuments.get(1), result2);
        ComposedDocumentDto result3 = service.composeAndSave(2, 2);
        verifyDto(fakeDocuments.get(2), result3);

        verify(composer).compose(datasets.get(0), templates.get(2));
        verify(composer).compose(datasets.get(1), templates.get(0));
        verify(composer).compose(datasets.get(2), templates.get(2));

        verify(dao, times(3)).save(any());
    }

    private void verifyDto(ComposedDocument document, ComposedDocumentDto dto) {
        assertNull(dto.getBytes());
        assertEquals(dto.getId(), document.getId());
        assertEquals(dto.getName(), document.getName());
        assertEquals(dto.getCreated(), document.getCreated());
        assertEquals(dto.getFormat(), document.getFormat());
        assertEquals(dto.getSize(), document.getSize());
        assertEquals(dto.getTemplateId(), document.getTemplate().getId());
        assertEquals(dto.getDatasetId(), document.getDataset().getId());
    }

    private static class ToDtoAnswer implements Answer<ComposedDocumentDto> {

        @Override
        public ComposedDocumentDto answer(InvocationOnMock invocationOnMock) throws Throwable {
            final ComposedDocument document = invocationOnMock.getArgument(0, ComposedDocument.class);

            final ComposedDocumentDto dto = new ComposedDocumentDto();
            dto.setId(document.getId());
            dto.setName(document.getName());
            dto.setCreated(document.getCreated());
            dto.setFormat(document.getFormat());
            dto.setSize(document.getSize());
            dto.setTemplateId(document.getTemplate().getId());
            dto.setDatasetId(document.getDataset().getId());

            return dto;
        }
    }
}