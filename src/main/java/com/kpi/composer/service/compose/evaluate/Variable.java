package com.kpi.composer.service.compose.evaluate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Variable<T> {

    private final String name;

    private T value;
}
