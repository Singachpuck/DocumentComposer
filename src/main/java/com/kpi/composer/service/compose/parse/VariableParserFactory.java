package com.kpi.composer.service.compose.parse;

import com.kpi.composer.exception.UnsupportedFormatException;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.entities.Dataset;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class VariableParserFactory implements ApplicationContextAware {

    private ApplicationContext ac;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    public VariableParser getVariableParser(Dataset dataset) {
        if (dataset.getFormat() == SupportedFormats.JSON) {
            return ac.getBean(JsonVariableParser.class);
        }

        throw new UnsupportedFormatException("Format " + dataset.getFormat() + " is not supported for dataset " + dataset.getName());
    }
}
