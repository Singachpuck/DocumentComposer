package com.kpi.composer.config;

import com.kpi.composer.service.compose.evaluate.InMemoryVariablePool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

@Configuration
public class ComposeConfig {

    @Bean
    public InMemoryVariablePool variablePool(ConversionService conversionService) {
        return InMemoryVariablePool.empty(conversionService);
    }
}
