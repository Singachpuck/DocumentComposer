package com.kpi.composer.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpi.composer.TestUtil;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.exception.MaxNumberExceededException;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.dto.DatasetDto;
import com.kpi.composer.model.dto.TemplateDto;
import com.kpi.composer.service.DatasetService;
import com.kpi.composer.service.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

// TODO: Test if entity belongs to the user when auth is enabled. Test if user is authorized
@WebMvcTest(value = DatasetRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class DatasetRestControllerTest {

    private List<DatasetDto> fakeDatasets = new ArrayList<>();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatasetService service;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.fakeDatasets = TestUtil.datasetDtoExample();
    }

    @Test
    void getAllTest() throws Exception {
        when(service.findAll()).thenReturn(fakeDatasets);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v1/datasets")
                .accept(SupportedFormats.JSON.getContentType());

        final String expectedPayload = objectMapper.writeValueAsString(fakeDatasets);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(SupportedFormats.JSON.getContentType()))
                .andExpect(content().json(expectedPayload, true));

        verify(service).findAll();
    }

    @Test
    void getAllEmptyTest() throws Exception {
        when(service.findAll()).thenReturn(Collections.emptyList());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v1/datasets")
                .accept(SupportedFormats.JSON.getContentType());

        final String expectedPayload = "[]";

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(SupportedFormats.JSON.getContentType()))
                .andExpect(content().json(expectedPayload, true));

        verify(service).findAll();
    }

    @Test
    void getTest() throws Exception {
        when(service.findDtoById(1L)).thenReturn(fakeDatasets.get(1));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v1/datasets/1")
                .accept(SupportedFormats.JSON.getContentType());

        final String expectedPayload = objectMapper.writeValueAsString(fakeDatasets.get(1));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(SupportedFormats.JSON.getContentType()))
                .andExpect(content().json(expectedPayload, true));

        verify(service).findDtoById(1L);
    }

    @Test
    void getNotExistTest() throws Exception {
        when(service.findDtoById(0L)).thenThrow(EntityException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v1/datasets/0")
                .accept(SupportedFormats.JSON.getContentType());

        final String errorPayload = "{status:\"KO\", type: \"Bad Request\"}";

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(SupportedFormats.JSON.getContentType()))
                .andExpect(content().json(errorPayload));

        verify(service).findDtoById(0L);
    }

    @Test
    void createTest() throws Exception {
        final DatasetDto target = new DatasetDto();
        target.setName("target");
        target.setFormat(SupportedFormats.JSON);
        target.setBytes(new byte[] {1, 2, 3, 4, 5});
        target.setCreated(ZonedDateTime.of(2023, 8, 10, 15, 30, 0, 0, ZoneId.systemDefault()));
        target.setSize(2048L);

        final DatasetDto result = new DatasetDto();
        result.setId(1L);
        result.setName("target");
        result.setFormat(SupportedFormats.JSON);
        result.setCreated(ZonedDateTime.of(2023, 8, 10, 15, 30, 0, 0, ZoneId.systemDefault()));
        result.setSize(2048L);

        ArgumentMatcher<DatasetDto> daoMatcher = t -> t.getName().equals(target.getName());

        when(service.create(argThat(daoMatcher))).thenReturn(result);

        final String contentPayload = objectMapper.writeValueAsString(target);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/v1/datasets")
                .contentType(SupportedFormats.JSON.getContentType())
                .content(contentPayload.getBytes())
                .accept(SupportedFormats.JSON.getContentType());

        final String expectedPayload = objectMapper.writeValueAsString(result);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(SupportedFormats.JSON.getContentType()))
                .andExpect(content().json(expectedPayload, true));

        verify(service).create(argThat(daoMatcher));
    }

    /**
     * Possible errors:
     * 1. Size limit exceeded.
     * 2. Amount of templates exceeded.
     * 3. Unsupported format
     *
     */
    @Test
    void unsupportedFormatTest() throws Exception {
        final DatasetDto target = fakeDatasets.get(0);
        target.setId(null);
        target.setFormat(SupportedFormats.DOCX);
        target.setBytes(new byte[] {1, 2, 3, 4, 5});

        final String contentPayload = objectMapper.writeValueAsString(target);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/v1/datasets")
                .contentType(SupportedFormats.JSON.getContentType())
                .content(contentPayload)
                .accept(SupportedFormats.JSON.getContentType());

        final String errorPayload = "{status:\"KO\", type:\"Bad Request\", description: \"Unsupported dataset format: DOCX\"}";

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(SupportedFormats.JSON.getContentType()))
                .andExpect(content().json(errorPayload, true));
    }

    @Test
    void sizeExceededTest() throws Exception {
        final DatasetDto target = fakeDatasets.get(0);
        target.setId(null);
        final byte[] bytes = new byte[1024*1024 + 1];
        for (int i = 0; i < 1024 * 1024 + 1; i++) {
            bytes[i] = (byte) (i % 127);
        }
        target.setBytes(bytes);


        final String contentPayload = objectMapper.writeValueAsString(target);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/v1/datasets")
                .contentType(SupportedFormats.JSON.getContentType())
                .content(contentPayload)
                .accept(SupportedFormats.JSON.getContentType());

        final String errorPayload = "{status:\"KO\", type:\"Bad Request\", description: \"Max dataset size exceeded: 1048576\"}";

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(SupportedFormats.JSON.getContentType()))
                .andExpect(content().json(errorPayload, true));
    }

    @Test
    void numberExceededTest() throws Exception {
        final DatasetDto target = fakeDatasets.get(0);
        target.setId(null);
        target.setBytes(new byte[] {1, 2, 3, 4, 5});

        when(service.create(any())).thenThrow(MaxNumberExceededException.class);

        final String contentPayload = objectMapper.writeValueAsString(target);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/v1/datasets")
                .contentType(SupportedFormats.JSON.getContentType())
                .content(contentPayload)
                .accept(SupportedFormats.JSON.getContentType());

        final String errorPayload = "{status:\"KO\", type:\"Forbidden\"}";

        mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(SupportedFormats.JSON.getContentType()))
                .andExpect(content().json(errorPayload));
    }
}