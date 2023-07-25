package com.kpi.composer.service.compose;

import com.kpi.composer.exception.ExpressionParseException;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.FileEntity;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.extract.Expression;
import com.kpi.composer.service.compose.extract.ExpressionExtractor;
import com.kpi.composer.service.compose.extract.Placeholder;
import com.kpi.composer.service.compose.parse.template.ExpressionParser;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Component
public class DocxComposer extends Composer {

    private final ExpressionParser expressionParser;

    private final ExpressionExtractor expressionExtractor;

    private final ConversionService conversionService;

    @Autowired
    public DocxComposer(ExpressionParser expressionParser,
                        ExpressionExtractor expressionExtractor,
                        ConversionService conversionService) {
        this.expressionParser = expressionParser;
        this.expressionExtractor = expressionExtractor;
        this.conversionService = conversionService;
    }

    @Override
    public FileEntity compose(Dataset dataset, Template template) {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(template.getBytes()))) {
            for (XWPFParagraph p : document.getParagraphs()) {
                List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    for (XWPFRun r : runs) {
                        final String text = r.getText(0);
                        if (text != null) {
                            r.setText(this.replaceText(text, template), 0);
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
                                    r.setText(this.replaceText(text, template), 0);
                                }
                            }
                        }
                    }
                }
            }

            document.write(new FileOutputStream("test1-output.docx"));
        } catch (IOException e) {}

        return null;
    }

    private String replaceText(String source, Template template) {
        final StringBuilder replacedTextSb = new StringBuilder(source);
        final Iterator<Expression> expressionIterator = expressionExtractor.extract(source,
                new Placeholder(template.getBeginTokenPlaceholder(), template.getEndTokenPlaceholder()),
                new Placeholder(template.getBeginEscapePlaceholder(), template.getEndEscapePlaceholder()));
        while (expressionIterator.hasNext()) {
            Expression expression = expressionIterator.next();

            if (!expression.isEscaped()) {
                final Object parsed = expressionParser.parse(expression.getValue());
                final String replacement = conversionService.convert(parsed, String.class);
                if (replacement == null) {
                    throw new ExpressionParseException("Can not cast parsed object to String");
                }
                replacedTextSb.replace(expression.getStart(), expression.getEnd(), replacement);
            }
        }
        return replacedTextSb.toString();
    }
}
