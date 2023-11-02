package com.kpi.composer.service.compose;

import com.kpi.composer.model.entities.ComposedDocument;
import com.kpi.composer.model.entities.Dataset;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.parse.VariableParserFactory;
import com.kpi.composer.service.compose.replace.MultiPartTextReplacer;
import com.kpi.composer.service.mapper.FileMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Composer {

    protected final VariableParserFactory variableParserFactory;

    protected final ConversionService conversionService;

    protected final MultiPartTextReplacer textReplacer;

    protected final FileMapper fileMapper;

    public abstract ComposedDocument compose(Dataset dataset, Template template);
}
