package com.kpi.composer.service.compose;

import com.kpi.composer.exception.UnsupportedFormatException;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.FileEntity;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import com.kpi.composer.service.compose.parse.dataset.VariableParser;
import com.kpi.composer.service.compose.parse.template.ExpressionParser;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

//@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Composer {

//    protected final Dataset dataset;
//
//    protected final Template template;

    public abstract FileEntity compose(Dataset dataset, Template template);

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
//            throw new UnsupportedFormatException("Can not compose " + templateInstance.getFormat().name() + " file");
//        }
//    }
}
