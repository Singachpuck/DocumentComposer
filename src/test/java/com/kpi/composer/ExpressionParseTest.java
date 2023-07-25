package com.kpi.composer;

import com.kpi.composer.service.compose.evaluate.InMemoryVariablePool;
import com.kpi.composer.service.compose.evaluate.Variable;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import com.kpi.composer.service.compose.parse.template.BinaryParseTree;
import com.kpi.composer.service.compose.parse.template.ExpressionParser;
import com.kpi.composer.service.compose.parse.template.token.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionParseTest {

    private VariablePool variablePool;

    @BeforeEach
    void setUp() {
        final Collection<Variable<?>> variables = List.of(
                new Variable<>("string", "string"),
                new Variable<>("long", 10L),
                new Variable<>("double", 10D)
        );
        variablePool = InMemoryVariablePool.load(variables);
    }


    static Stream<Arguments> expressions() {
        return Stream.of(
                Arguments.arguments("(10 + 15) / 5", 5L),
                Arguments.arguments("(2022 - 2004)", 18L),
                Arguments.arguments("(3 * (2 + 4) - 8) / (2*3 - 1)", 2L),
                Arguments.arguments("'I' + ' ' + 'love' + ' ' + 'Ukraine' + 10", "I love Ukraine10")
        );
    }

    @ParameterizedTest
    @MethodSource("expressions")
    void t1(String expression, Object result) {
        final ExpressionParser ep = new ExpressionParser(variablePool);
        Object r = ep.parse(expression);
        assertEquals(result, r);
    }
}
