package com.kpi.composer.service.compose.parse.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class VariableToken extends Token implements Serializable {

    private final String variableName;

    @Override
    public String toString() {
        return "VariableToken(" + variableName + ")";
    }
}
