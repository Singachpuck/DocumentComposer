package com.kpi.composer.model;

import org.springframework.http.MediaType;

import java.util.Collections;

public enum SupportedFormats {
    DOCX("docx", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")), JSON("json", MediaType.APPLICATION_JSON);

    private final String value;

    private final MediaType contentType;

    SupportedFormats(String value, MediaType contentType) {
        this.value = value;
        this.contentType = contentType;
    }

    public String getValue() {
        return value;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public static Iterable<SupportedFormats> getDataSupportedFormats() {
        return Collections.singletonList(JSON);
    }

    public static Iterable<SupportedFormats> getTemplateSupportedFormats() {
        return Collections.singletonList(DOCX);
    }
}
