package com.kpi.composer.service.compose.evaluate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryVariablePoolTest {

    private Collection<Variable<?>> variables;

    @BeforeEach
    void setUp() {
        variables = Set.of(
                new Variable<>("long", 10L),
                new Variable<>("string", "str"),
                new Variable<>("double", 10.5D)
        );
    }

    @Test
    void t1() {
        final InMemoryVariablePool variablePool = InMemoryVariablePool.load(variables);

        Variable<String> string = variablePool.lookup("string", String.class);
        Variable<Long> integerVariable = variablePool.lookup("long", Long.class);
        Variable<Double> doubleVariable = variablePool.lookup("double", Double.class);

        assertEquals(10, integerVariable.getValue());
        assertEquals("str", string.getValue());
        assertEquals(10.5D, doubleVariable.getValue());
    }

}