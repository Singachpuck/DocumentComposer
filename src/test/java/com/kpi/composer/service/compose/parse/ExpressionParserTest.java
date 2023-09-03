package com.kpi.composer.service.compose.parse;

import com.kpi.composer.TestUtil;
import com.kpi.composer.service.compose.evaluate.BPTEvaluator;
import com.kpi.composer.service.compose.evaluate.InMemoryVariablePool;
import com.kpi.composer.service.compose.evaluate.Variable;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionParserTest {

    private final TokenExtractor tokenExtractor = new SimpleTokenExtractor();

    private final BPTEvaluator evaluator = new BPTEvaluator();

    private final ExpressionParser ep = new ExpressionParser(tokenExtractor, evaluator);

    private VariablePool variablePool;

    @BeforeEach
    void setUp() {
        final Collection<Variable<?>> variables = TestUtil.variableExample();
        variablePool = InMemoryVariablePool.load(variables, null);
    }


    static Stream<Arguments> expressions() {
        return Stream.of(
                Arguments.of("(10 + 15) / 5", 5L),
                Arguments.of("(2022 - 2004)", 18L),
                Arguments.of("(3 * (2 + 4) - 8) / (2*3 - 1)", 2L),
                Arguments.of("   (long - double * ((100.5) + (40))) / double * long    ", -1395.47619048D),
                Arguments.of("   (long - double * ((100.5) + (40))) / double + long    ", -129.547619048D),
                Arguments.of("   (201) / double * long    ", 191.428571429D),
                Arguments.of("10 - 5 + 5", 10L),
                Arguments.of("1 + 2* 3 -long", -3L),
                // Negative values are not supported!
                Arguments.of("0-10", -10L)
        );
    }

    @ParameterizedTest
    @MethodSource("expressions")
    void parseTest(String expression, Object result) {
        Object r = ep.parse(expression, variablePool);
        if (result instanceof Double) {
            assertEquals((double) result, (double) r, 10e-7);
        } else {
            assertEquals(result, r);
        }
    }

    static Stream<Arguments> stringExpressions() {
        return Stream.of(
                Arguments.of("'I' + ' ' + 'love' + ' ' + 'Ukraine'", "I love Ukraine"),
                Arguments.of("10 + 'I' + ' ' + 'love' + ' ' + 'Ukraine' + 10.85", "10I love Ukraine10.85"),
                Arguments.of("10 + 'I' + ' ' + 'love' + 3 + ' ' + 'Ukraine' + 10.85", "10I love3 Ukraine10.85"),
                Arguments.of("'2+10I' + ' ' + 'love+-/*[]' + ' ' + '.1020Ukraine)()'", "2+10I love+-/*[] .1020Ukraine)()"),
                Arguments.of("(20 / (10 + 5)) + ' ' + 'love' + ' ' + 'Ukraine ' + (20 / (10 + 5)* 100) + '%'", "1 love Ukraine 100%")
        );
    }

    @ParameterizedTest
    @MethodSource("stringExpressions")
    void parseStringTest(String expression, Object result) {
        Object r = ep.parse(expression, variablePool);
        assertEquals(result, r);
    }
}
