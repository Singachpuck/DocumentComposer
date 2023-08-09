package com.kpi.composer.service.compose.parse;

import com.kpi.composer.service.compose.evaluate.Variable;

import java.util.Collection;

public interface VariableParser {

    Collection<Variable<?>> parse(byte[] bytes);
}
