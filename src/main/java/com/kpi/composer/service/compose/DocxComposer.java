package com.kpi.composer.service.compose;

import com.kpi.composer.exception.DocumentComposingException;
import com.kpi.composer.exception.FormatMismatchException;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.evaluate.InMemoryVariablePool;
import com.kpi.composer.service.compose.evaluate.Variable;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import com.kpi.composer.service.compose.extract.Placeholder;
import com.kpi.composer.service.compose.parse.VariableParserFactory;
import com.kpi.composer.service.compose.replace.DocxTextReplacer;
import com.kpi.composer.service.compose.replace.TextHolder;
import com.kpi.composer.service.mapper.FileMapper;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

// TODO: TESTED
@Component
public class DocxComposer extends Composer {

    @Autowired
    protected DocxComposer(VariableParserFactory variableParserFactory,
                           ConversionService conversionService,
                           DocxTextReplacer docxTextReplacer,
                           FileMapper fileMapper) {
        super(variableParserFactory, conversionService, docxTextReplacer, fileMapper);
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
                this.replaceInParagraph(p, template, variablePool);
            }

            for (XWPFTable tbl : document.getTables()) {
                for (XWPFTableRow row : tbl.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            this.replaceInParagraph(p, template, variablePool);
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

    private void replaceInParagraph(XWPFParagraph p, Template template, VariablePool variablePool) {
        final List<XWPFRun> runs = p.getRuns();
        if (runs != null && !runs.isEmpty()) {
            final List<TextHolder> docxTextHolders = runs
                    .stream()
                    .map(r -> (TextHolder) new DocxTextReplacer.DocxTextHolder(r))
                    .toList();
            final Placeholder tokenPholder = new Placeholder(template.getBeginTokenPlaceholder(),
                    template.getEndTokenPlaceholder());
            final Placeholder escapePholder = new Placeholder(template.getBeginEscapePlaceholder(),
                    template.getEndEscapePlaceholder());
            textReplacer.replaceParts(docxTextHolders, tokenPholder, escapePholder, variablePool);
        }
    }

    private ComposedDocument composeDocxDocument(Template template, Dataset dataset, byte[] bytes) {
        final ComposedDocument document = fileMapper.documentFromParams(template, dataset, bytes);
        document.setName(document.getName() + "." + SupportedFormats.DOCX.getValue());
        document.setFormat(SupportedFormats.DOCX);
        return document;
    }
}
