package com.kpi.composer.service.compose;

import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.extract.ExpressionExtractor;
import com.kpi.composer.service.compose.parse.ExpressionParser;
import com.kpi.composer.service.compose.parse.VariableParserFactory;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Composer {

    protected final VariableParserFactory variableParserFactory;

    protected final ExpressionParser expressionParser;

    protected final ExpressionExtractor expressionExtractor;

    protected final ConversionService conversionService;

    protected final FileMapper fileMapper;

    public abstract ComposedDocument compose(Dataset dataset, Template template);

//    protected void updateVariablePool(Dataset dataset) {
//        final Collection<Variable<?>> variables = variableParserFactory
//                .getVariableParser(dataset)
//                .parse(dataset.getBytes());
//        variablePool.replace(variables);
//    }
//    static class Builder {
//
//        private Dataset datasetInstance;
//
//        private Template templateInstance;
//
//        public Builder withDataset(Dataset dataset) {
//            this.datasetInstance = dataset;
//            return this;
//        }
//
//        public Builder withTemplate(Template template) {
//            this.templateInstance = template;
//            return this;
//        }
//
//        public Composer build() {
//            if (templateInstance.getFormat() == SupportedFormats.DOCX) {
//                return new DocxComposer(datasetInstance, templateInstance);
//            }
//
//            throw new UnsupportedFormatException("Can not composeAndSave " + templateInstance.getFormat().name() + " file");
//        }
//    }
}
