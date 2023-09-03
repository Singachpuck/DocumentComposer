package com.kpi.composer.service;

import com.kpi.composer.TestUtil;
import com.kpi.composer.dao.TemplateDao;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.exception.MaxNumberExceededException;
import com.kpi.composer.model.dto.TemplateDto;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.mapper.FileMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    private static final List<Template> fakeTemplates = TestUtil.templateExample();

    private static final ToDtoAnswer toDtoAnswer = new ToDtoAnswer();

    @InjectMocks
    private TemplateService service;

    @Mock
    private TemplateDao dao;

    @Mock
    private FileMapper mapper;

    @Test
    void findAllTest() {
        when(dao.findAll()).thenReturn(fakeTemplates);
        when(mapper.templateToDto(isA(Template.class))).thenAnswer(toDtoAnswer);

        final Collection<TemplateDto> result = service.findAll();

        assertEquals(3, result.size());
        int i = 0;
        for (TemplateDto dto : result) {
            final Template template = fakeTemplates.get(i++);
            this.verifyDto(template, dto);
        }

        verify(mapper, times(3)).templateToDto(any());
    }

    @Test
    void findByIdTest() {
        when(dao.findById(1L)).thenReturn(Optional.ofNullable(fakeTemplates.get(1)));
        final Template template = service.findById(1L);
        assertEquals(template, fakeTemplates.get(1));
        verify(dao).findById(1L);
    }

    @Test
    void findDtoByIdTest() {
        when(dao.findById(1L)).thenReturn(Optional.ofNullable(fakeTemplates.get(1)));
        when(mapper.templateToDto(isA(Template.class))).thenAnswer(toDtoAnswer);
        final TemplateDto dto = service.findDtoById(1L);
        verifyDto(fakeTemplates.get(1), dto);
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
    void createTest() {
        final Template template = fakeTemplates.get(2);
        final TemplateDto target = new TemplateDto();
        target.setName(template.getName());
        target.setCreated(template.getCreated());
        target.setFormat(template.getFormat());
        target.setBytes(template.getBytes());
        target.setSize(template.getSize());
        target.setBeginTokenPlaceholder(template.getBeginTokenPlaceholder());
        target.setEndTokenPlaceholder(template.getEndTokenPlaceholder());
        target.setBeginEscapePlaceholder(template.getBeginEscapePlaceholder());
        target.setEndEscapePlaceholder(template.getEndEscapePlaceholder());

        ReflectionTestUtils.setField(service, "templateMaxNumber", 10L);
        when(mapper.dtoToTemplate(target)).thenReturn(template);
        when(mapper.templateToDto(isA(Template.class))).thenAnswer(toDtoAnswer);
        when(dao.save(template)).thenReturn(template);

        final TemplateDto result = service.create(target);
        assertTrue(new ReflectionEquals(target, "bytes", "id").matches(result));
        assertEquals(template.getId(), result.getId());
    }

    @Test
    void numberExceededTest() {
        final Template template = fakeTemplates.get(2);
        final TemplateDto target = new TemplateDto();
        target.setName(template.getName());
        target.setCreated(template.getCreated());
        target.setFormat(template.getFormat());
        target.setBytes(template.getBytes());
        target.setSize(template.getSize());
        target.setBeginTokenPlaceholder(template.getBeginTokenPlaceholder());
        target.setEndTokenPlaceholder(template.getEndTokenPlaceholder());
        target.setBeginEscapePlaceholder(template.getBeginEscapePlaceholder());
        target.setEndEscapePlaceholder(template.getEndEscapePlaceholder());

        ReflectionTestUtils.setField(service, "templateMaxNumber", 10L);
        when(dao.count()).thenReturn(10L);

        assertThrows(MaxNumberExceededException.class, () -> service.create(target));
    }

    private void verifyDto(Template template, TemplateDto dto) {
        assertNull(dto.getBytes());
        assertEquals(dto.getId(), template.getId());
        assertEquals(dto.getName(), template.getName());
        assertEquals(dto.getCreated(), template.getCreated());
        assertEquals(dto.getFormat(), template.getFormat());
        assertEquals(dto.getSize(), template.getSize());
        assertEquals(dto.getBeginTokenPlaceholder(), template.getBeginTokenPlaceholder());
        assertEquals(dto.getEndTokenPlaceholder(), template.getEndTokenPlaceholder());
        assertEquals(dto.getBeginEscapePlaceholder(), template.getBeginEscapePlaceholder());
        assertEquals(dto.getEndEscapePlaceholder(), template.getEndEscapePlaceholder());
    }

    private static class ToDtoAnswer implements Answer<TemplateDto> {

        @Override
        public TemplateDto answer(InvocationOnMock invocationOnMock) throws Throwable {
            final Template template = invocationOnMock.getArgument(0, Template.class);

            final TemplateDto dto = new TemplateDto();
            dto.setId(template.getId());
            dto.setName(template.getName());
            dto.setCreated(template.getCreated());
            dto.setFormat(template.getFormat());
            dto.setSize(template.getSize());
            dto.setBeginTokenPlaceholder(template.getBeginTokenPlaceholder());
            dto.setEndTokenPlaceholder(template.getEndTokenPlaceholder());
            dto.setBeginEscapePlaceholder(template.getBeginEscapePlaceholder());
            dto.setEndEscapePlaceholder(template.getEndEscapePlaceholder());

            return dto;
        }
    }

}