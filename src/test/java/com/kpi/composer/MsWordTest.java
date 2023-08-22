package com.kpi.composer;

import com.kpi.composer.service.compose.evaluate.InMemoryVariablePool;
import com.kpi.composer.service.compose.evaluate.Variable;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import com.kpi.composer.service.compose.extract.DefaultExpressionExtractor;
import com.kpi.composer.service.compose.extract.ExpressionExtractor;
import com.kpi.composer.service.compose.extract.Placeholder;
import com.kpi.composer.service.compose.parse.ExpressionParser;
import com.kpi.composer.service.compose.replace.DocxTextReplacer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class MsWordTest {

    @ParameterizedTest
    @ValueSource(strings = "test1.docx")
    void touch(String path) throws IOException, InvalidFormatException, URISyntaxException {
        URL resourceUrl = getClass().getClassLoader().getResource(path);
        try (XWPFDocument document = new XWPFDocument(OPCPackage.open(new File(resourceUrl.toURI())))) {
            for (XWPFParagraph p : document.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null && text.contains("${}")) {
                            text = text.replace("${}", "${replaced}");//your content
                            r.setText(text, 0);
                        }
                    }
                }
            }

            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            for (XWPFRun r : p.getRuns()) {
                                String text = r.getText(0);
                                if (text != null && text.contains("${}")) {
                                    text = text.replace("${}", "${replaced}");
                                    r.setText(text, 0);
                                }
                            }
                        }
                    }
                }
            }

            document.write(new FileOutputStream("test1-output.docx"));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = "test2.docx")
    void setTest(String path) throws IOException, InvalidFormatException, URISyntaxException {
        URL resourceUrl = getClass().getClassLoader().getResource(path);
        try (XWPFDocument document = new XWPFDocument(OPCPackage.open(new File(resourceUrl.toURI())))) {
            for (XWPFParagraph p : document.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null) {
                            r.setText("New text.", 0);
                        }
                    }
                }
            }

            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            for (XWPFRun r : p.getRuns()) {
                                String text = r.getText(0);
                                if (text != null) {
                                    r.setText("N", 0);
                                }
                            }
                        }
                    }
                }
            }

            document.write(new FileOutputStream("test2-output.docx"));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = "test2.docx")
    void replaceTest(String source) throws URISyntaxException, InvalidFormatException, IOException {
        URL resourceUrl = getClass().getClassLoader().getResource(source);
        try (XWPFDocument document = new XWPFDocument(OPCPackage.open(new File(resourceUrl.toURI())))) {
            for (XWPFParagraph p : document.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        String text = r.getText(0);
                        if (text != null) {
                            r.setText("New text.", 1);
                        }
                    }
                }
            }

            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            for (XWPFRun r : p.getRuns()) {
                                String text = r.getText(0);
                                if (text != null) {
                                    r.setText("#", 1);
                                }
                            }
                        }
                    }
                }
            }

            document.write(new FileOutputStream("test2-output.docx"));
        }
    }

//    @ParameterizedTest
//    @ValueSource(strings = "test2.docx")
//    void anotherWordTest(String source) throws URISyntaxException, InvalidFormatException, IOException {
//        URL resourceUrl = getClass().getClassLoader().getResource(source);
//        try (XWPFDocument document = new XWPFDocument(OPCPackage.open(new File(resourceUrl.toURI())))) {
//
//        }
//    }

    @ParameterizedTest
    @ValueSource(strings = "test3.docx")
    void replacerTest(String source) throws URISyntaxException, InvalidFormatException, IOException {
        final ExpressionExtractor ee = new DefaultExpressionExtractor();
        final ExpressionParser expressionParser = new ExpressionParser();
        final DocxTextReplacer textReplacer = new DocxTextReplacer(ee, expressionParser, new PseudoConversionService());

        //     "string1": "string1",
        //    "string2": "string2",
        //    "number1": 10,
        //    "number2": 20,
        //    "float1": 10.5,
        //    "float2": 20.5,
        //    "object1": {
        //        "name": "value"
        //    },
        //    "array1": [10, "dedq", 15.5, 16]

        final VariablePool variablePool = InMemoryVariablePool.load(List.of(
                new Variable<>("string1", "string1"),
                new Variable<>("string2", "string2"),
                new Variable<>("number1", 10),
                new Variable<>("number2", 20),
                new Variable<>("float1", 10.5),
                new Variable<>("float2", 20.5),
                new Variable<>("object1", "{\"name\": \"value\"}"),
                new Variable<>("array1", "[10, \"dedq\", 15.5, 16]")
        ), null);

        URL resourceUrl = getClass().getClassLoader().getResource(source);
        try (XWPFDocument document = new XWPFDocument(OPCPackage.open(new File(resourceUrl.toURI())))) {
            for (XWPFParagraph p : document.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null && !runs.isEmpty()) {
//                    runs.get(0).get
                    final List<DocxTextReplacer.DocxTextHolder> docxTextHolders = runs
                            .stream()
                            .map(DocxTextReplacer.DocxTextHolder::new)
                            .toList();
                    final Placeholder tokenPholder = new Placeholder("${",
                            "}");
                    final Placeholder escapePholder = new Placeholder("\"",
                            "\"");
                    textReplacer.replaceParts(docxTextHolders, tokenPholder, escapePholder, variablePool);
                }
            }

            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            List<XWPFRun> runs = p.getRuns();
                            if (runs != null && !runs.isEmpty()) {
                                final List<DocxTextReplacer.DocxTextHolder> docxTextHolders = runs
                                        .stream()
                                        .map(DocxTextReplacer.DocxTextHolder::new)
                                        .toList();
                                final Placeholder tokenPholder = new Placeholder("${",
                                        "}");
                                final Placeholder escapePholder = new Placeholder("\"",
                                        "\"");
                                textReplacer.replaceParts(docxTextHolders, tokenPholder, escapePholder, variablePool);
                            }
                        }
                    }
                }
            }

            document.write(new FileOutputStream("test2-output.docx"));
        }
    }

    static class PseudoConversionService implements ConversionService {

        @Override
        public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
            return true;
        }

        @Override
        public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return true;
        }

        @Override
        public <T> T convert(Object source, Class<T> targetType) {
            if (targetType == String.class) {
                return (T) source.toString();
            }
            return null;
        }

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            return source.toString();
        }
    }
}
