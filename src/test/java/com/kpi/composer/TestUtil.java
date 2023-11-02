package com.kpi.composer;

import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.dto.ComposedDocumentDto;
import com.kpi.composer.model.dto.DatasetDto;
import com.kpi.composer.model.dto.TemplateDto;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.evaluate.Variable;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestUtil {

    public static List<TemplateDto> templateDtoExample() {
        final TemplateDto t1 = new TemplateDto();
        t1.setId(0L);
        t1.setName("t1");
        t1.setFormat(SupportedFormats.DOCX);
        t1.setBeginTokenPlaceholder("${");
        t1.setEndTokenPlaceholder("}");
        t1.setBeginEscapePlaceholder("\\");
        t1.setEndEscapePlaceholder("\\");
        t1.setCreated(ZonedDateTime.of(2023, 8, 10, 20, 0, 0, 0, ZoneId.systemDefault()));
        t1.setSize(1024L);

        final TemplateDto t2 = new TemplateDto();
        t2.setId(1L);
        t2.setName("t2");
        t2.setFormat(SupportedFormats.DOCX);
        t2.setBeginTokenPlaceholder("(");
        t2.setEndTokenPlaceholder(")");
        t2.setBeginEscapePlaceholder("\\");
        t2.setEndEscapePlaceholder("\\");
        t2.setCreated(ZonedDateTime.of(2023, 8, 10, 15, 30, 0, 0, ZoneId.systemDefault()));
        t2.setSize(2048L);

        final TemplateDto t3 = new TemplateDto();
        t3.setId(3L);
        t3.setName("t3");
        t3.setFormat(SupportedFormats.DOCX);
        t3.setBeginTokenPlaceholder("#");
        t3.setEndTokenPlaceholder("#");
        t3.setBeginEscapePlaceholder("\\");
        t3.setEndEscapePlaceholder("\\");
        t3.setCreated(ZonedDateTime.of(2023, 8, 10, 20, 0, 0, 0, ZoneId.systemDefault()));
        t3.setSize(4096L);

        return List.of(t1, t2, t3);
    }

    public static List<DatasetDto> datasetDtoExample() {
        final DatasetDto t1 = new DatasetDto();
        t1.setId(0L);
        t1.setName("t1");
        t1.setFormat(SupportedFormats.JSON);
        t1.setCreated(ZonedDateTime.of(2023, 8, 10, 20, 0, 0, 0, ZoneId.systemDefault()));
        t1.setSize(1024L);

        final DatasetDto t2 = new DatasetDto();
        t2.setId(1L);
        t2.setName("t2");
        t2.setFormat(SupportedFormats.JSON);
        t2.setCreated(ZonedDateTime.of(2023, 8, 10, 15, 30, 0, 0, ZoneId.systemDefault()));
        t2.setSize(2048L);

        final DatasetDto t3 = new DatasetDto();
        t3.setId(3L);
        t3.setName("t3");
        t3.setFormat(SupportedFormats.JSON);
        t3.setCreated(ZonedDateTime.of(2023, 8, 10, 20, 0, 0, 0, ZoneId.systemDefault()));
        t3.setSize(4096L);

        return List.of(t1, t2, t3);
    }

    public static List<ComposedDocumentDto> documentDtoExample() {
        final List<Template> templates = templateExample();
        final List<Dataset> datasets = datasetExample();

        final ComposedDocumentDto d1 = new ComposedDocumentDto();
        d1.setId(0L);
        d1.setName("d1");
        d1.setFormat(SupportedFormats.DOCX);
        d1.setCreated(ZonedDateTime.of(2023, 8, 10, 14, 0, 0, 0, ZoneId.systemDefault()));
        d1.setSize(1024L);
        d1.setDatasetId(datasets.get(0).getId());
        d1.setTemplateId(templates.get(2).getId());

        final ComposedDocumentDto d2 = new ComposedDocumentDto();
        d2.setId(1L);
        d2.setName("d2");
        d2.setFormat(SupportedFormats.DOCX);
        d2.setCreated(ZonedDateTime.of(2022, 7, 10, 10, 30, 0, 0, ZoneId.systemDefault()));
        d2.setSize(2048L);
        d2.setDatasetId(datasets.get(1).getId());
        d2.setTemplateId(templates.get(0).getId());

        final ComposedDocumentDto d3 = new ComposedDocumentDto();
        d3.setId(2L);
        d3.setName("d3");
        d3.setFormat(SupportedFormats.DOCX);
        d3.setCreated(ZonedDateTime.of(2023, 8, 20, 2, 0, 0, 0, ZoneId.systemDefault()));
        d3.setSize(4096L);
        d3.setDatasetId(datasets.get(2).getId());
        d3.setTemplateId(templates.get(2).getId());

        return List.of(d1, d2, d3);
    }

    public static List<Dataset> datasetExample() {
        final Dataset d1 = new Dataset();
        d1.setId(1L);
        d1.setName("d1");
        d1.setFormat(SupportedFormats.JSON);
        d1.setBytes(new byte[] {1, 2, 3, 4, 5});
        d1.setCreated(ZonedDateTime.of(2023, 8, 10, 14, 0, 0, 0, ZoneId.systemDefault()));
        d1.setSize(1024L);

        final Dataset d2 = new Dataset();
        d2.setId(2L);
        d2.setName("d2");
        d2.setFormat(SupportedFormats.JSON);
        d2.setBytes(new byte[] {100, 101, 102, 103, 104, 123});
        d2.setCreated(ZonedDateTime.of(2022, 7, 10, 10, 30, 0, 0, ZoneId.systemDefault()));
        d2.setSize(2048L);

        final Dataset d3 = new Dataset();
        d3.setId(3L);
        d3.setName("d3");
        d3.setFormat(SupportedFormats.JSON);
        d3.setBytes(new byte[] {127, 32, 4, 13, 94, 3});
        d3.setCreated(ZonedDateTime.of(2023, 8, 20, 2, 0, 0, 0, ZoneId.systemDefault()));
        d3.setSize(4096L);

        return List.of(d1, d2, d3);
    }

    public static List<Template> templateExample() {
        final Template t1 = new Template();
        t1.setId(1L);
        t1.setName("t1");
        t1.setFormat(SupportedFormats.DOCX);
        t1.setBytes(new byte[] {1, 2, 3, 4, 5});
        t1.setBeginTokenPlaceholder("${");
        t1.setEndTokenPlaceholder("}");
        t1.setBeginEscapePlaceholder("\\");
        t1.setEndEscapePlaceholder("\\");
        t1.setCreated(ZonedDateTime.of(2023, 8, 10, 20, 0, 0, 0, ZoneId.systemDefault()));
        t1.setSize(1024L);

        final Template t2 = new Template();
        t2.setId(2L);
        t2.setName("t2");
        t2.setFormat(SupportedFormats.DOCX);
        t2.setBytes(new byte[] {100, 101, 102, 103, 104, 123});
        t2.setBeginTokenPlaceholder("(");
        t2.setEndTokenPlaceholder(")");
        t2.setBeginEscapePlaceholder("\\");
        t2.setEndEscapePlaceholder("\\");
        t2.setCreated(ZonedDateTime.of(2023, 8, 10, 15, 30, 0, 0, ZoneId.systemDefault()));
        t2.setSize(2048L);

        final Template t3 = new Template();
        t3.setId(3L);
        t3.setName("t3");
        t3.setFormat(SupportedFormats.DOCX);
        t3.setBytes(new byte[] {127, 32, 4, 13, 94, 3});
        t3.setBeginTokenPlaceholder("#");
        t3.setEndTokenPlaceholder("#");
        t3.setBeginEscapePlaceholder("\\");
        t3.setEndEscapePlaceholder("\\");
        t3.setCreated(ZonedDateTime.of(2023, 8, 10, 20, 0, 0, 0, ZoneId.systemDefault()));
        t3.setSize(4096L);

        return List.of(t1, t2, t3);
    }

    public static List<ComposedDocument> documentExample() {
        final List<Template> templates = templateExample();
        final List<Dataset> datasets = datasetExample();

        final ComposedDocument d1 = new ComposedDocument();
        d1.setId(1L);
        d1.setName("d1");
        d1.setFormat(SupportedFormats.DOCX);
        d1.setBytes(new byte[] {127, 32, 4, 13, 94, 3});
        d1.setCreated(ZonedDateTime.of(2023, 8, 10, 14, 0, 0, 0, ZoneId.systemDefault()));
        d1.setSize(1024L);
        d1.setDataset(datasets.get(0));
        d1.setTemplate(templates.get(2));

        final ComposedDocument d2 = new ComposedDocument();
        d2.setId(2L);
        d2.setName("d2");
        d2.setFormat(SupportedFormats.DOCX);
        d2.setBytes(new byte[] {1, 2, 3, 4, 5});
        d2.setCreated(ZonedDateTime.of(2022, 7, 10, 10, 30, 0, 0, ZoneId.systemDefault()));
        d2.setSize(2048L);
        d2.setDataset(datasets.get(1));
        d2.setTemplate(templates.get(0));

        final ComposedDocument d3 = new ComposedDocument();
        d3.setId(3L);
        d3.setName("d3");
        d3.setFormat(SupportedFormats.DOCX);
        d3.setBytes(new byte[] {100, 101, 102, 103, 104, 123});
        d3.setCreated(ZonedDateTime.of(2023, 8, 20, 2, 0, 0, 0, ZoneId.systemDefault()));
        d3.setSize(4096L);
        d3.setDataset(datasets.get(2));
        d3.setTemplate(templates.get(2));

        return List.of(d1, d2, d3);
    }



    public static Set<Variable<?>> variableExample() {
        return Set.of(
                new Variable<>("long", 10L),
                new Variable<>("string", "200"),
                new Variable<>("double", 10.5D)
        );
    }


    public static ConversionService conversionServiceMock() {
        final ConversionService mock = Mockito.mock(ConversionService.class);

        when(mock.convert(any(), any())).thenAnswer(invocation -> {
            Object source = invocation.getArgument(0);
            Class<?> desired = invocation.getArgument(1, Class.class);
            if (desired == Long.class) {
                if (source instanceof Long) {
                    return source;
                } else if (source instanceof Double d) {
                    return d.longValue();
                } else if (source instanceof String s) {
                    return Long.valueOf(s);
                }
            } else if (desired == Double.class) {
                if (source instanceof Long l) {
                    return l.doubleValue();
                } else if (source instanceof Double d) {
                    return d;
                } else if (source instanceof String s) {
                    return Double.valueOf(s);
                }
            } else {
                return String.valueOf(source);
            }
            return null;
        });

        return mock;
    }

    public static byte[] readResource(String file) throws URISyntaxException, IOException {
        URL resource = TestUtil.class.getClassLoader().getResource(file);
        return Files.readAllBytes(Paths.get(resource.toURI()));
    }
}
