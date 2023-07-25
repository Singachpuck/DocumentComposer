package com.kpi.composer.service.compose.evaluate;

import com.kpi.composer.exception.VariableCastException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.convert.ConversionService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryVariablePool implements VariablePool {

    private final Map<String, Variable<?>> pool = new HashMap<>();

    private final ConversionService conversionService;

    private InMemoryVariablePool(Collection<Variable<?>> variables, ConversionService conversionService) {
        this.conversionService = conversionService;
        this.replace(variables);
    }

    public static InMemoryVariablePool empty(ConversionService conversionService) {
        return new InMemoryVariablePool(Collections.emptyList(), conversionService);
    }

    public static InMemoryVariablePool load(Collection<Variable<?>> variables, ConversionService conversionService) {
        return new InMemoryVariablePool(variables, conversionService);
    }

    private static Map<String, Variable<?>> variablesToMap(Collection<Variable<?>> variables) {
        return variables
                .stream()
                .collect(Collectors.toMap(Variable::getName, Function.identity()));
    }

    @Override
    public Variable<?> lookup(String name) {
        final Variable<Object> lookupVariable = new Variable<>(name);
        final Variable<?> variable = pool.get(name);
        if (variable == null) {
            return null;
        }
        lookupVariable.setValue(variable.getValue());
        return lookupVariable;
    }

    @Override
    public <T> Variable<T> lookup(String name, Class<T> type) {
        if (!this.AVAILABLE_TYPES.contains(type)) {
            throw new VariableCastException("Unrecognized type: " + type.getName());
        }

        final Variable<T> lookupVariable = new Variable<>(name);
        final Variable<?> variable = pool.get(name);
        if (variable == null) {
            return null;
        }
        if (conversionService == null) {
            lookupVariable.setValue(type.cast(variable.getValue()));
        } else {
            lookupVariable.setValue(conversionService.convert(variable.getValue(), type));
        }
        return lookupVariable;
    }

    @Override
    public void replace(Collection<Variable<?>> variables) {
        pool.clear();
        pool.putAll(variablesToMap(variables));
    }
}
