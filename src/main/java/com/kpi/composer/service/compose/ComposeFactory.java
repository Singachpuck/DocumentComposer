package com.kpi.composer.service.compose;

import com.kpi.composer.exception.UnsupportedFormatException;
import com.kpi.composer.model.SupportedFormats;
import com.kpi.composer.model.entities.Template;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ComposeFactory implements ApplicationContextAware {

    private ApplicationContext ac;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    public Composer getComposer(Template template) {
        if (template.getFormat() == SupportedFormats.DOCX) {
            return ac.getBean(DocxComposer.class);
        }

        throw new UnsupportedFormatException("Format " + template.getFormat() + " is not supported for template " + template.getName());
    }


}
