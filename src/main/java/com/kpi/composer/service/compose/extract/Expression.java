package com.kpi.composer.service.compose.extract;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Expression {

    private final String value;

    private final int start;

    private final int end;

    private final Placeholder artifacts;

    private final boolean isEscaped;

}
