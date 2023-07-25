package com.kpi.composer.service.compose.extract;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;

@Getter
@RequiredArgsConstructor
public abstract class ExpressionExtractor {

//    public abstract Iterator<Expression> extract(String text);

    public abstract Iterator<Expression> extract(String text, Placeholder tokenPlaceholder, Placeholder escapePlaceholder);
}
