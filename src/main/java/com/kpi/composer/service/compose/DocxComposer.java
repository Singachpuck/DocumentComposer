package com.kpi.composer.service.compose;

import com.kpi.composer.exception.DocumentComposingException;
import com.kpi.composer.exception.ExpressionParseException;
import com.kpi.composer.exception.FormatMismatchException;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.evaluate.InMemoryVariablePool;
import com.kpi.composer.service.compose.evaluate.Variable;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import com.kpi.composer.service.compose.extract.Expression;
import com.kpi.composer.service.compose.extract.ExpressionExtractor;
import com.kpi.composer.service.compose.extract.Placeholder;
import com.kpi.composer.service.compose.parse.ExpressionParser;
import com.kpi.composer.service.compose.parse.VariableParserFactory;
import com.kpi.composer.service.compose.replace.DocxTextReplacer;
import com.kpi.composer.service.mapper.FileMapper;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Component
public class DocxComposer extends Composer {

    @Autowired
    protected DocxComposer(VariableParserFactory variableParserFactory,
                           ExpressionParser expressionParser,
                           ExpressionExtractor expressionExtractor,
                           ConversionService conversionService,
                           DocxTextReplacer docxTextReplacer,
                           FileMapper fileMapper) {
        super(variableParserFactory, expressionParser, expressionExtractor, conversionService, docxTextReplacer, fileMapper);
    }

    @Override
    public ComposedDocument compose(Dataset dataset, Template template) {
        if (template.getFormat() != SupportedFormats.DOCX) {
            throw new FormatMismatchException("Expected format: DOCX. Received: " + template.getFormat());
        }

        final Collection<Variable<?>> variables = variableParserFactory
                .getVariableParser(dataset)
                .parse(dataset.getBytes());
        final VariablePool variablePool = InMemoryVariablePool.load(variables, conversionService);

        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(template.getBytes()));
            ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            for (XWPFParagraph p : document.getParagraphs()) {
                final List<XWPFRun> runs = p.getRuns();
                if (runs != null) {
                    final List<DocxTextReplacer.DocxTextHolder> docxTextHolders = runs
                            .stream()
                            .map(DocxTextReplacer.DocxTextHolder::new)
                            .toList();
                    final Placeholder tokenPholder = new Placeholder(template.getBeginTokenPlaceholder(),
                            template.getEndTokenPlaceholder());
                    final Placeholder escapePholder = new Placeholder(template.getBeginEscapePlaceholder(),
                            template.getEndEscapePlaceholder());
                    docxTextReplacer.replaceParts(docxTextHolders, tokenPholder, escapePholder, variablePool);
//                    for (XWPFRun r : runs) {
//                        final String text = r.getText(0);
//                        if (text != null) {
//                            r.setText(this.replaceText(text, template, variablePool), 0);
//                        }
//                    }
                }
            }

            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            final List<XWPFRun> runs = p.getRuns();
                            if (runs != null && !runs.isEmpty()) {
                                final List<DocxTextReplacer.DocxTextHolder> docxTextHolders = runs
                                        .stream()
                                        .map(DocxTextReplacer.DocxTextHolder::new)
                                        .toList();
                                final Placeholder tokenPholder = new Placeholder(template.getBeginTokenPlaceholder(),
                                        template.getEndTokenPlaceholder());
                                final Placeholder escapePholder = new Placeholder(template.getBeginEscapePlaceholder(),
                                        template.getEndEscapePlaceholder());
                                docxTextReplacer.replaceParts(docxTextHolders, tokenPholder, escapePholder, variablePool);
//                            for (XWPFRun r : p.getRuns()) {
//                                String text = r.getText(0);
//                                if (text != null) {
//                                    r.setText(this.replaceText(text, template, variablePool), 0);
//                                }
//                            }
                            }
                        }
                    }
                }
            }

            document.write(output);
            return this.composeDocxDocument(template, dataset, output.toByteArray());
        } catch (IOException e) {
            throw new DocumentComposingException("Error occurred during document composing.", e);
        }
    }

    private String replaceText(String source, Template template, VariablePool variablePool) {
        final StringBuilder replacedTextSb = new StringBuilder(source);
        final Iterator<Expression> expressionIterator = expressionExtractor.extract(source,
                new Placeholder(template.getBeginTokenPlaceholder(), template.getEndTokenPlaceholder()),
                new Placeholder(template.getBeginEscapePlaceholder(), template.getEndEscapePlaceholder()));
        while (expressionIterator.hasNext()) {
            Expression expression = expressionIterator.next();

            if (!expression.isEscaped()) {
                final Object parsed = expressionParser.parse(expression.getValue(), variablePool);
                final String replacement = conversionService.convert(parsed, String.class);
                if (replacement == null) {
                    throw new ExpressionParseException("Can not cast parsed object to String");
                }
                replacedTextSb.replace(expression.getStart(), expression.getEnd(), replacement);
            }
        }
        return replacedTextSb.toString();
    }

    private ComposedDocument composeDocxDocument(Template template, Dataset dataset, byte[] bytes) {
        final ComposedDocument document = fileMapper.documentFromParams(template, dataset, bytes);
        document.setName(document.getName() + "." + SupportedFormats.DOCX.getValue());
        document.setFormat(SupportedFormats.DOCX);
        return document;
    }
}
