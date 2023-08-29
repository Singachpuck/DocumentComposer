package com.kpi.composer.service.compose.parse;

import com.kpi.composer.exception.ExpressionParseException;
import com.kpi.composer.service.compose.Operators;
import com.kpi.composer.service.compose.parse.token.LiteralToken;
import com.kpi.composer.service.compose.parse.token.OperatorToken;
import com.kpi.composer.service.compose.parse.token.Token;
import com.kpi.composer.service.compose.parse.token.VariableToken;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SimpleTokenExtractorTest {

    private final TokenExtractor tokenExtractor = new SimpleTokenExtractor();

    static Stream<Arguments> expressions() {
        return Stream.of(
                Arguments.of("10.5 + 40", List.of(
                        new LiteralToken<>(10.5),
                        new OperatorToken(Operators.PLUS),
                        new LiteralToken<>(40L))),
                Arguments.of("10.5 + 40.9", List.of(
                        new LiteralToken<>(10.5),
                        new OperatorToken(Operators.PLUS),
                        new LiteralToken<>(40.9))),
                Arguments.of("(target1 - target2 * ((100.5) + (40))) / target3 * target4", List.of(
                        new OperatorToken(Operators.GROUP_OPEN),
                        new VariableToken("target1"),
                        new OperatorToken(Operators.MINUS),
                        new VariableToken("target2"),
                        new OperatorToken(Operators.MULTIPLY),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new LiteralToken<>(100.5),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.PLUS),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new LiteralToken<>(40L),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.DIVIDE),
                        new VariableToken("target3"),
                        new OperatorToken(Operators.MULTIPLY),
                        new VariableToken("target4")
                )),
                Arguments.of("'Begin text, ' + 'text with \\'escape\\', ' + 'end text.'", List.of(
                        new LiteralToken<>("Begin text, "),
                        new OperatorToken(Operators.PLUS),
                        new LiteralToken<>("text with 'escape', "),
                        new OperatorToken(Operators.PLUS),
                        new LiteralToken<>("end text.")
                )),
                Arguments.of("10 / ('Begin text, ' + 'end text.') - ' /|\\\\\\\\\\\\\\\\\\\\ '", List.of(
                        new LiteralToken<>(10L),
                        new OperatorToken(Operators.DIVIDE),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new LiteralToken<>("Begin text, "),
                        new OperatorToken(Operators.PLUS),
                        new LiteralToken<>("end text."),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.MINUS),
                        new LiteralToken<>(" /|\\\\\\\\\\ ")
                )),
                Arguments.of("- 10 (*    variable (( 'string'", List.of(
                        new OperatorToken(Operators.MINUS),
                        new LiteralToken<>(10L),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new OperatorToken(Operators.MULTIPLY),
                        new VariableToken("variable"),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new LiteralToken<>("string")
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("expressions")
    void extractTest(String expression, List<Token> result) {
        final List<Token> tokens = tokenExtractor.extractTokens(expression);

        int i = 0;
        for (Token token : tokens) {
            if (token instanceof LiteralToken<?> lt) {
                assertEquals(lt.getValue(), ((LiteralToken) result.get(i)).getValue());
            } else if (token instanceof VariableToken vt) {
                assertEquals(vt.getVariableName(), ((VariableToken) result.get(i)).getVariableName());
            } else if (token instanceof OperatorToken ot) {
                assertEquals(ot.getOperator(), ((OperatorToken) result.get(i)).getOperator());
            }

            i++;
        }
    }

    static Stream<Arguments> errorExpressions() {
        return Stream.of(
                Arguments.of("10var"),
                Arguments.of("var\\'iable"),
                Arguments.of("var\\iable"),
                Arguments.of("var1\\0iable"),
                Arguments.of("var1\\+iable"),
                Arguments.of("var1\\ iable"),
                Arguments.of("var1\\.iable"),
                Arguments.of("var'iable"),
                Arguments.of("100000'000000"),
                Arguments.of("10.1 + 2023.2000.0000"),
                Arguments.of("var.iable"),
                Arguments.of("10%00"),
                Arguments.of("10'00'"),
                Arguments.of("'string literal"),
                Arguments.of("'\\\\\\\\\\\\\\\\\\'")
        );
    }

    @ParameterizedTest
    @MethodSource("errorExpressions")
    void negativeTesting(String expression) {
        assertThrows(ExpressionParseException.class, () -> tokenExtractor.extractTokens(expression));
    }

}