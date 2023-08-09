package com.kpi.composer.service.compose.evaluate;

import com.kpi.composer.service.compose.parse.token.LiteralToken;

import java.util.Collection;
import java.util.Set;

public interface VariablePool {

    Set<Class<?>> AVAILABLE_TYPES = Set.of(Long.class, Double.class, String.class);

    <T> Variable<T> lookup(String name, Class<T> type);

    Variable<?> lookup(String name);

    default <T> LiteralToken<T> lookupLiteral(String name, Class<T> type) {
        final Variable<T> variable = this.lookup(name, type);
        return new LiteralToken<>(variable.getValue());
    }

    default LiteralToken<?> lookupLiteral(String name) {
        final Variable<?> variable = this.lookup(name);
        return new LiteralToken<>(variable.getValue());
    }

    void replace(Collection<Variable<?>> variables);
}
