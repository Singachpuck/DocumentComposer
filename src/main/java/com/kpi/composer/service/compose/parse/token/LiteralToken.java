package com.kpi.composer.service.compose.parse.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class LiteralToken<T> extends Token implements Serializable {

    private final T value;

    @Override
    public String toString() {
        return "LiteralToken(" + value + ")";
    }
}
