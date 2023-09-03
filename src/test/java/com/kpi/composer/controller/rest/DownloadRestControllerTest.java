package com.kpi.composer.controller.rest;

import com.kpi.composer.TestUtil;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.dto.ComposedDocumentDto;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.service.ComposeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = DownloadRestController.class)
class DownloadRestControllerTest {

    private List<ComposedDocument> fakeDocuments = new ArrayList<>();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComposeService composeService;

    @BeforeEach
    void setup() {
        this.fakeDocuments = TestUtil.documentExample();
    }

    @Test
    void downloadComposedTest() throws Exception {
        when(composeService.findById(0L)).thenReturn(fakeDocuments.get(0));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v1/download/compose/0")
                .accept(SupportedFormats.DOCX.getContentType());

        final byte[] expectedPayload = fakeDocuments.get(0).getBytes();

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(SupportedFormats.DOCX.getContentType()))
                .andExpect(content().bytes(expectedPayload));
    }
}