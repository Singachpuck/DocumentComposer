package com.kpi.composer.service.compose;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpi.composer.TestUtil;
import com.kpi.composer.exception.DocumentComposingException;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.evaluate.*;
import com.kpi.composer.service.compose.extract.DefaultExpressionExtractor;
import com.kpi.composer.service.compose.extract.ExpressionExtractor;
import com.kpi.composer.service.compose.parse.*;
import com.kpi.composer.service.compose.replace.DocxTextReplacer;
import com.kpi.composer.service.mapper.FileMapper;
import com.kpi.composer.service.mapper.FileMapperImpl;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocxComposerTest {

    @Mock
    private VariableParserFactory variableParserFactory;

    @Mock
    private FileMapper fileMapper;

    private final ConversionService conversionService = TestUtil.conversionServiceMock();

    private final ExpressionExtractor expressionExtractor = new DefaultExpressionExtractor();

    private final TokenExtractor tokenExtractor = new SimpleTokenExtractor();

    private final Evaluator<BinaryParseTree> evaluator = new BPTEvaluator();

    private final ExpressionParser expressionParser = new ExpressionParser(tokenExtractor, evaluator);

    private final DocxTextReplacer docxTextReplacer = new DocxTextReplacer(expressionExtractor, expressionParser, conversionService);

    private List<Variable<?>> variables;

    private Composer composer;

    @BeforeEach
    void setup() {
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonVariableParser jsonVariableParser = new JsonVariableParser(objectMapper);
        when(variableParserFactory.getVariableParser(any(Dataset.class)))
                .thenReturn(jsonVariableParser);

        when(fileMapper.documentFromParams(any(), any(), any())).thenAnswer((invocation -> {
            final ComposedDocument composedDocument = new ComposedDocument();
            final Template template = invocation.getArgument(0, Template.class);
            final Dataset dataset = invocation.getArgument(1, Dataset.class);
            final byte[] bytes = invocation.getArgument(2, byte[].class);

            if ( template == null && dataset == null && bytes == null ) {
                return null;
            }

            composedDocument.setTemplate( template );
            composedDocument.setDataset( dataset );
            composedDocument.setName( template.getName() + " - " + dataset.getName() );
            composedDocument.setCreated( ZonedDateTime.now() );
            composedDocument.setBytes( bytes );
            composedDocument.setSize( bytes.length );

            return composedDocument;
        }));

        this.composer = new DocxComposer(variableParserFactory, conversionService, docxTextReplacer, fileMapper);
    }

    @ParameterizedTest
    @CsvSource({"docx/test2.docx,json/testDataset3.json,(,),6", "docx/test3.docx,json/testDataset3.json,${,},10"})
    void compositionTest(String template, String dataset, String phldrBegin, String phldrEnd, int i) throws URISyntaxException, IOException {
        this.variables = List.of(
                new Variable<>("string1", "replaced1"),
                new Variable<>("string2", "replaced2"),
                new Variable<>("float1", 1.1),
                new Variable<>("float2", 2.2),
                new Variable<>("array1", "[1,2,3]"),
                new Variable<>("object1", "{\"name\":\"user1\"}")
        );

        final Template t = new Template();
        final byte[] templateBytes = TestUtil.readResource(template);
        t.setId(1L);
        t.setName("template1");
        t.setFormat(SupportedFormats.DOCX);
        t.setBytes(templateBytes);
        t.setBeginTokenPlaceholder(phldrBegin);
        t.setEndTokenPlaceholder(phldrEnd);
        t.setBeginEscapePlaceholder("\\");
        t.setEndEscapePlaceholder("\\");
        t.setCreated(ZonedDateTime.now());
        t.setSize(templateBytes.length);

        final Dataset d = new Dataset();
        final byte[] datasetBytes = TestUtil.readResource(dataset);
        d.setId(1L);
        d.setName("dataset1");
        d.setFormat(SupportedFormats.JSON);
        d.setBytes(datasetBytes);
        d.setCreated(ZonedDateTime.now());
        d.setSize(datasetBytes.length);
        final ComposedDocument composedDocument = composer.compose(d, t);

        assertEquals("template1 - dataset1.docx", composedDocument.getName());
        assertEquals(SupportedFormats.DOCX, composedDocument.getFormat());
        assertNull(composedDocument.getId());
        assertEquals(t, composedDocument.getTemplate());
        assertEquals(d, composedDocument.getDataset());

        this.verifyComposed(composedDocument.getBytes(), t, i);

        verify(variableParserFactory).getVariableParser(eq(d));
        verify(fileMapper).documentFromParams(eq(t), eq(d), eq(composedDocument.getBytes()));
    }

    private void verifyComposed(byte[] bytes, Template t, int n) {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(bytes))) {
            int i = 0;
            Map<String, Boolean> variableMap = variables
                    .stream()
                    .collect(Collectors.toMap(Variable::getName, v -> false));
            for (XWPFParagraph p : document.getParagraphs()) {
                for (XWPFRun run : p.getRuns()) {
                    final String text = run.getText(0);
                    if (text != null) {
                        assertFalse(text.contains(t.getBeginEscapePlaceholder()));
                        assertFalse(text.contains(t.getEndEscapePlaceholder()));
                        assertFalse(text.contains(t.getBeginTokenPlaceholder()));
                        assertFalse(text.contains(t.getEndTokenPlaceholder()));
                        for (Variable<?> variable : variables) {
                            assertFalse(text.contains(variable.getName()));
                            if (text.contains(variable.getValue().toString())) {
                                i++;
                            }
                            variableMap.put(variable.getName(), true);
                        }
                    }
                }
            }

            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            for (XWPFRun run : p.getRuns()) {
                                final String text = run.getText(0);
                                if (text != null) {
                                    assertFalse(text.contains(t.getBeginEscapePlaceholder()));
                                    assertFalse(text.contains(t.getEndEscapePlaceholder()));
                                    for (Variable<?> variable : variables) {
                                        assertFalse(text.contains(variable.getName()));
                                        if (text.contains(variable.getValue().toString())) {
                                            i++;
                                        }
                                        variableMap.put(variable.getName(), true);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            assertEquals(0, variableMap.values().stream().filter(b -> !b).count());
            assertEquals(n, i);
        } catch (IOException e) {
            throw new DocumentComposingException("Error occurred during document composing.", e);
        }
    }
}