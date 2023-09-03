package com.kpi.composer.service;

import com.kpi.composer.TestUtil;
import com.kpi.composer.dao.DatasetDao;
import com.kpi.composer.dao.TemplateDao;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.exception.MaxNumberExceededException;
import com.kpi.composer.model.dto.DatasetDto;
import com.kpi.composer.model.dto.TemplateDto;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.mapper.FileMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
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
class DatasetServiceTest {

    private static final List<Dataset> fakeDatasets = TestUtil.datasetExample();

    private static final ToDtoAnswer toDtoAnswer = new ToDtoAnswer();

    @InjectMocks
    private DatasetService service;

    @Mock
    private DatasetDao dao;

    @Mock
    private FileMapper mapper;

    @Test
    void findAllTest() {
        when(dao.findAll()).thenReturn(fakeDatasets);
        when(mapper.datasetToDto(isA(Dataset.class))).thenAnswer(toDtoAnswer);

        final Collection<DatasetDto> result = service.findAll();

        assertEquals(3, result.size());
        int i = 0;
        for (DatasetDto dto : result) {
            final Dataset dataset = fakeDatasets.get(i++);
            this.verifyDto(dataset, dto);
        }

        verify(mapper, times(3)).datasetToDto(any());
    }

    @Test
    void findByIdTest() {
        when(dao.findById(1L)).thenReturn(Optional.ofNullable(fakeDatasets.get(1)));
        final Dataset template = service.findById(1L);
        assertEquals(template, fakeDatasets.get(1));
        verify(dao).findById(1L);
    }

    @Test
    void findDtoByIdTest() {
        when(dao.findById(1L)).thenReturn(Optional.ofNullable(fakeDatasets.get(1)));
        when(mapper.datasetToDto(isA(Dataset.class))).thenAnswer(toDtoAnswer);
        final DatasetDto dto = service.findDtoById(1L);
        verifyDto(fakeDatasets.get(1), dto);
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
        final Dataset dataset = fakeDatasets.get(2);
        final DatasetDto target = new DatasetDto();
        target.setName(dataset.getName());
        target.setCreated(dataset.getCreated());
        target.setFormat(dataset.getFormat());
        target.setBytes(dataset.getBytes());
        target.setSize(dataset.getSize());

        ReflectionTestUtils.setField(service, "datasetMaxNumber", 25L);
        when(mapper.dtoToDataset(target)).thenReturn(dataset);
        when(mapper.datasetToDto(isA(Dataset.class))).thenAnswer(toDtoAnswer);
        when(dao.save(dataset)).thenReturn(dataset);

        final DatasetDto result = service.create(target);
        assertTrue(new ReflectionEquals(target, "bytes", "id").matches(result));
        assertEquals(dataset.getId(), result.getId());
    }

    @Test
    void numberExceededTest() {
        final Dataset dataset = fakeDatasets.get(2);
        final DatasetDto target = new DatasetDto();
        target.setName(dataset.getName());
        target.setCreated(dataset.getCreated());
        target.setFormat(dataset.getFormat());
        target.setBytes(dataset.getBytes());
        target.setSize(dataset.getSize());

        ReflectionTestUtils.setField(service, "datasetMaxNumber", 25L);
        when(dao.count()).thenReturn(25L);

        assertThrows(MaxNumberExceededException.class, () -> service.create(target));
    }

    private void verifyDto(Dataset template, DatasetDto dto) {
        assertNull(dto.getBytes());
        assertEquals(dto.getId(), template.getId());
        assertEquals(dto.getName(), template.getName());
        assertEquals(dto.getCreated(), template.getCreated());
        assertEquals(dto.getFormat(), template.getFormat());
        assertEquals(dto.getSize(), template.getSize());
    }

    private static class ToDtoAnswer implements Answer<DatasetDto> {

        @Override
        public DatasetDto answer(InvocationOnMock invocationOnMock) throws Throwable {
            final Dataset template = invocationOnMock.getArgument(0, Dataset.class);

            final DatasetDto dto = new DatasetDto();
            dto.setId(template.getId());
            dto.setName(template.getName());
            dto.setCreated(template.getCreated());
            dto.setFormat(template.getFormat());
            dto.setSize(template.getSize());

            return dto;
        }
    }
}