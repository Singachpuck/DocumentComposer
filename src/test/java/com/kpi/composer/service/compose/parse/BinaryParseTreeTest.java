package com.kpi.composer.service.compose.parse;

import com.kpi.composer.service.compose.Operators;
import com.kpi.composer.service.compose.parse.template.BinaryParseTree;
import com.kpi.composer.service.compose.parse.template.ExpressionParser;
import com.kpi.composer.service.compose.parse.template.token.LiteralToken;
import com.kpi.composer.service.compose.parse.template.token.OperatorToken;
import com.kpi.composer.service.compose.parse.template.token.Token;
import com.kpi.composer.service.compose.parse.template.token.VariableToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

class BinaryParseTreeTest {

    static Stream<Arguments> tokens() {
        return Stream.of(
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
            ), Operators.PLUS),
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
            ), Operators.MINUS)
        );
    }

    @ParameterizedTest
    @MethodSource("tokens")
    void t1(List<Token> tokens, Operators targetOperator) {
        BinaryParseTree parseTree = BinaryParseTree.build(tokens);
        BinaryParseTree.Node result = parseTree.extract();

        Assertions.assertNotNull(result.getLeft());
        Assertions.assertNotNull(result.getRight());
        Assertions.assertInstanceOf(OperatorToken.class, result.getToken());
        Assertions.assertEquals(targetOperator, ((OperatorToken) result.getToken()).getOperator());
    }
}