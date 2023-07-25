package com.kpi.composer.config;

import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.DatasetService;
import com.kpi.composer.service.TemplateService;
import com.kpi.composer.service.Uploader;
import com.kpi.composer.service.converter.ToDoubleConverter;
import com.kpi.composer.service.converter.ToIntegerConverter;
import com.kpi.composer.service.converter.ToStringConverter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class Config implements WebMvcConfigurer {

    private TemplateService templateService;

    private DatasetService datasetService;

    @Override
    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(new ToStringConverter());
//        registry.addConverter(new ToDoubleConverter());
//        registry.addConverter(new ToIntegerConverter());
    }

    @Bean
    Uploader templateUploader() {
        return new Uploader(Template.class, templateService::create);
    }

    @Bean
    Uploader datasetUploader() {
        return new Uploader(Dataset.class, datasetService::create);
    }
}
