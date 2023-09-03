package com.kpi.composer.config;

import com.kpi.composer.service.DatasetService;
import com.kpi.composer.service.TemplateService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
public class Config implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverter(new ToStringConverter());
//        registry.addConverter(new ToDoubleConverter());
//        registry.addConverter(new ToIntegerConverter());
    }

//    @Bean
//    Uploader templateUploader() {
//        return new Uploader(Template.class, templateService::create);
//    }
//
//    @Bean
//    Uploader datasetUploader() {
//        return new Uploader(Dataset.class, datasetService::create);
//    }
}
