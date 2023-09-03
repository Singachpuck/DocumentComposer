package com.kpi.composer.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpi.composer.TestUtil;
import com.kpi.composer.exception.EntityException;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.dto.ComposedDocumentDto;
import com.kpi.composer.model.dto.DatasetDto;
import com.kpi.composer.service.ComposeService;
import com.kpi.composer.service.DatasetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ComposedDocumentRestController.class)
class ComposedDocumentRestControllerTest {

    private List<ComposedDocumentDto> fakeDocuments = new ArrayList<>();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComposeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.fakeDocuments = TestUtil.documentDtoExample();
    }

    @Test
    void getAllTest() throws Exception {
        when(service.findAll()).thenReturn(fakeDocuments);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v1/compose")
                .accept(SupportedFormats.JSON.getContentType());

        final String expectedPayload = objectMapper.writeValueAsString(fakeDocuments);

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
                .get("/api/v1/compose")
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
        when(service.findDtoById(2L)).thenReturn(fakeDocuments.get(2));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v1/compose/2")
                .accept(SupportedFormats.JSON.getContentType());

        final String expectedPayload = objectMapper.writeValueAsString(fakeDocuments.get(2));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(SupportedFormats.JSON.getContentType()))
                .andExpect(content().json(expectedPayload, true));

        verify(service).findDtoById(2L);
    }

    @Test
    void getNotExistTest() throws Exception {
        when(service.findDtoById(0L)).thenThrow(EntityException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v1/compose/0")
                .accept(SupportedFormats.JSON.getContentType());

        final String errorPayload = "{status:\"KO\", type: \"Bad Request\"}";

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(SupportedFormats.JSON.getContentType()))
                .andExpect(content().json(errorPayload));

        verify(service).findDtoById(0L);
    }

    @Test
    void composeTest() throws Exception {
        when(service.composeAndSave(2, 0)).thenReturn(fakeDocuments.get(0));
        when(service.composeAndSave(0, 1)).thenReturn(fakeDocuments.get(1));
        when(service.composeAndSave(2, 2)).thenReturn(fakeDocuments.get(2));

        final ComposedDocumentDto payload1 = new ComposedDocumentDto();
        payload1.setTemplateId(fakeDocuments.get(0).getTemplateId());
        payload1.setDatasetId(fakeDocuments.get(0).getDatasetId());

        final ComposedDocumentDto payload2 = new ComposedDocumentDto();
        payload2.setTemplateId(fakeDocuments.get(1).getTemplateId());
        payload2.setDatasetId(fakeDocuments.get(1).getDatasetId());

        final ComposedDocumentDto payload3 = new ComposedDocumentDto();
        payload3.setTemplateId(fakeDocuments.get(2).getTemplateId());
        payload3.setDatasetId(fakeDocuments.get(2).getDatasetId());

        int i = 0;
        for (ComposedDocumentDto dto : List.of(payload1, payload2, payload3)) {
            final String contentPayload = objectMapper.writeValueAsString(dto);

            RequestBuilder request = MockMvcRequestBuilders
                    .post("/api/v1/compose")
                    .contentType(SupportedFormats.JSON.getContentType())
                    .content(contentPayload.getBytes())
                    .accept(SupportedFormats.JSON.getContentType());

            final String expectedPayload = objectMapper.writeValueAsString(fakeDocuments.get(i++));

            mockMvc.perform(request)
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(SupportedFormats.JSON.getContentType()))
                    .andExpect(content().json(expectedPayload, true));
        }

        verify(service).composeAndSave(2, 0);
        verify(service).composeAndSave(0, 1);
        verify(service).composeAndSave(2, 2);
    }
}