package com.kpi.composer.service.compose.parse.template.token;

import com.kpi.composer.service.compose.Operators;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class OperatorToken extends Token implements Serializable {

    private final Operators operator;

    @Override
    public String toString() {
        return "OperatorToken(" + operator.name() + ")";
    }
}
