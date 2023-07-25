package com.kpi.composer;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
}
