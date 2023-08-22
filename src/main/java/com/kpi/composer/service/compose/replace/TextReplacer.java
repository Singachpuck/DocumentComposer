package com.kpi.composer.service.compose.replace;

import com.kpi.composer.service.compose.extract.ExpressionExtractor;
import com.kpi.composer.service.compose.parse.ExpressionParser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;


@RequiredArgsConstructor
public abstract class TextReplacer {

    protected final ExpressionExtractor expressionExtractor;

    protected final ExpressionParser expressionParser;

    protected final ConversionService conversionService;
}
