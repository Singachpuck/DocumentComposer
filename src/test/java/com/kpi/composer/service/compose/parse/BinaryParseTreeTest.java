package com.kpi.composer.service.compose.parse;

import com.kpi.composer.exception.ExpressionParseException;
import com.kpi.composer.service.compose.Operators;
import com.kpi.composer.service.compose.parse.token.LiteralToken;
import com.kpi.composer.service.compose.parse.token.OperatorToken;
import com.kpi.composer.service.compose.parse.token.Token;
import com.kpi.composer.service.compose.parse.token.VariableToken;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class BinaryParseTreeTest {

    static Stream<Arguments> tokens() {
        return Stream.of(
            Arguments.of(List.of(
                    new VariableToken("abc"),
                    new OperatorToken(Operators.PLUS),
                    new VariableToken("def")
            ), Operators.PLUS),
            // (target1 - target2 * ((100.5) + (40))) / target3 * target4
            Arguments.of(List.of(
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
                    new LiteralToken<>(40),
                    new OperatorToken(Operators.GROUP_CLOSE),
                    new OperatorToken(Operators.GROUP_CLOSE),
                    new OperatorToken(Operators.GROUP_CLOSE),
                    new OperatorToken(Operators.DIVIDE),
                    new VariableToken("target3"),
                    new OperatorToken(Operators.MULTIPLY),
                    new VariableToken("target4")
            ), Operators.MULTIPLY),
            Arguments.arguments(List.of(
                    new OperatorToken(Operators.GROUP_OPEN),
                    new VariableToken("a"),
                    new OperatorToken(Operators.PLUS),
                    new VariableToken("e"),
                    new OperatorToken(Operators.MINUS),
                    new OperatorToken(Operators.GROUP_OPEN),
                    new VariableToken("b"),
                    new OperatorToken(Operators.MINUS),
                    new VariableToken("c"),
                    new OperatorToken(Operators.PLUS),
                    new VariableToken("d"),
                    new OperatorToken(Operators.GROUP_CLOSE),
                    new OperatorToken(Operators.GROUP_CLOSE)
            ), Operators.MINUS),
            Arguments.arguments(List.of(
                    new OperatorToken(Operators.GROUP_OPEN),
                    new OperatorToken(Operators.GROUP_OPEN),
                    new VariableToken("a"),
                    new OperatorToken(Operators.PLUS),
                    new VariableToken("b"),
                    new OperatorToken(Operators.GROUP_CLOSE),
                    new OperatorToken(Operators.MULTIPLY),
                    new VariableToken("c"),
                    new OperatorToken(Operators.MINUS),
                    new VariableToken("e"),
                    new OperatorToken(Operators.MULTIPLY),
                    new VariableToken("f"),
                    new OperatorToken(Operators.GROUP_CLOSE)
            ), Operators.MINUS),
            Arguments.of(List.of(
                    new OperatorToken(Operators.GROUP_OPEN),
                    new OperatorToken(Operators.GROUP_OPEN),
                    new LiteralToken<>(100.5),
                    new OperatorToken(Operators.GROUP_CLOSE),
                    new OperatorToken(Operators.PLUS),
                    new OperatorToken(Operators.GROUP_OPEN),
                    new LiteralToken<>("str"),
                    new OperatorToken(Operators.GROUP_CLOSE),
                    new OperatorToken(Operators.GROUP_CLOSE)
            ), Operators.PLUS),
            // (target1 - target2 * ((100.5) + (40))) / target3 + target4
            Arguments.of(List.of(
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
                    new LiteralToken<>(40),
                    new OperatorToken(Operators.GROUP_CLOSE),
                    new OperatorToken(Operators.GROUP_CLOSE),
                    new OperatorToken(Operators.GROUP_CLOSE),
                    new OperatorToken(Operators.DIVIDE),
                    new VariableToken("target3"),
                    new OperatorToken(Operators.PLUS),
                    new VariableToken("target4")
            ), Operators.PLUS)
        );
    }

    @ParameterizedTest
    @MethodSource("tokens")
    void extractTest(List<Token> tokens, Operators targetOperator) {
        final BinaryParseTree parseTree = BinaryParseTree.build(tokens);
        final BinaryParseTree.Node result = parseTree.extract();

        assertNotNull(result.getLeft());
        assertNotNull(result.getRight());
        assertInstanceOf(OperatorToken.class, result.getToken());
        assertEquals(targetOperator, ((OperatorToken) result.getToken()).getOperator());
    }

    static Stream<Arguments> unbalancedTokens() {
        return Stream.of(
                // (target1 - target2 * ((100.5) + (40))_ / target3 * target4
                Arguments.of(List.of(
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
                        new LiteralToken<>(40),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.DIVIDE),
                        new VariableToken("target3"),
                        new OperatorToken(Operators.MULTIPLY),
                        new VariableToken("target4")
                )),
                // (target1 - target2 * ((100.5)(40))) / target3 * target4
                Arguments.of(List.of(
                        new OperatorToken(Operators.GROUP_OPEN),
                        new VariableToken("target1"),
                        new OperatorToken(Operators.MINUS),
                        new VariableToken("target2"),
                        new OperatorToken(Operators.MULTIPLY),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new LiteralToken<>(100.5),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new LiteralToken<>(40),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.DIVIDE),
                        new VariableToken("target3"),
                        new OperatorToken(Operators.MULTIPLY),
                        new VariableToken("target4")
                )),
                // (target1 "target2" * ((100.5) + (40))_ / target3 * target4
                Arguments.of(List.of(
                        new OperatorToken(Operators.GROUP_OPEN),
                        new VariableToken("target1"),
                        new LiteralToken<>("target2"),
                        new OperatorToken(Operators.MULTIPLY),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new LiteralToken<>(100.5),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.PLUS),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new LiteralToken<>(40),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.DIVIDE),
                        new VariableToken("target3"),
                        new OperatorToken(Operators.MULTIPLY),
                        new VariableToken("target4")
                )),
                // (target1 _ target2 * ((100.5) + (40))) target3 * target4
                Arguments.of(List.of(
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
                        new LiteralToken<>(40),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new VariableToken("target3"),
                        new OperatorToken(Operators.MULTIPLY),
                        new VariableToken("target4")
                )),
                // (target1 _ target2 * ((100.5) + (40))) / target3 target4
                Arguments.of(List.of(
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
                        new LiteralToken<>(40),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.DIVIDE),
                        new VariableToken("target3"),
                        new VariableToken("target4")
                )),
                // (- target2 * ((100.5) + (40))) / target3
                Arguments.of(List.of(
                        new OperatorToken(Operators.GROUP_OPEN),
                        new OperatorToken(Operators.MINUS),
                        new VariableToken("target2"),
                        new OperatorToken(Operators.MULTIPLY),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new LiteralToken<>(100.5),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.PLUS),
                        new OperatorToken(Operators.GROUP_OPEN),
                        new LiteralToken<>(40),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new OperatorToken(Operators.DIVIDE),
                        new VariableToken("target3")
                )),
                Arguments.of(List.of(
                        new OperatorToken(Operators.MINUS),
                        new VariableToken("target2")
                )),
                Arguments.of(List.of(
                        new OperatorToken(Operators.GROUP_OPEN),
                        new OperatorToken(Operators.GROUP_CLOSE)
                )),
                Arguments.of(List.of(
                        new OperatorToken(Operators.GROUP_CLOSE),
                        new VariableToken("target2"),
                        new OperatorToken(Operators.GROUP_OPEN)
                )),
                Arguments.of(List.of())
                );
    }

    @ParameterizedTest
    @MethodSource("unbalancedTokens")
    void unbalancedTest(List<Token> tokens) {
        assertThrows(ExpressionParseException.class, () -> BinaryParseTree.build(tokens));
    }

    @Test
    void soleTest() {
        final List<Token> tokens = List.of(new VariableToken("str"));
        assertDoesNotThrow(() -> BinaryParseTree.build(tokens));

        final List<Token> tokens2 = List.of(
                new OperatorToken(Operators.GROUP_OPEN),
                new LiteralToken<>(100.5),
                new OperatorToken(Operators.GROUP_CLOSE)
        );
        assertDoesNotThrow(() -> BinaryParseTree.build(tokens2));
    }

    @Test
    void copyTest() {
        final List<Token> tokens = List.of(
                new VariableToken("abc"),
                new OperatorToken(Operators.PLUS),
                new VariableToken("def")
        );
        final BinaryParseTree parseTree = BinaryParseTree.build(tokens);
        final BinaryParseTree.Node result1 = parseTree.extract();
        final BinaryParseTree.Node result2 = parseTree.extract();
        final BinaryParseTree.Node copiedResult = parseTree.cloneAndExtract();

        assertSame(result1, result2);
        assertNotSame(result1, copiedResult);
        assertNotSame(result2, copiedResult);
    }
}