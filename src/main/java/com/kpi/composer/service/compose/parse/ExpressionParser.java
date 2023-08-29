package com.kpi.composer.service.compose.parse;

import com.kpi.composer.exception.ExpressionParseException;
import com.kpi.composer.exception.UnknownTokenTypeException;
import com.kpi.composer.exception.UnsupportedLiteralType;
import com.kpi.composer.service.compose.Operators;
import com.kpi.composer.service.compose.evaluate.BPTEvaluator;
import com.kpi.composer.service.compose.evaluate.Evaluator;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import com.kpi.composer.service.compose.parse.token.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// TODO: TESTED
@Component
@RequiredArgsConstructor
public class ExpressionParser {

    private final TokenExtractor tokenExtractor;

    private final Evaluator<BinaryParseTree> evaluator;

    public Object parse(String expression, VariablePool variablePool) {
        final BinaryParseTree parseTree = BinaryParseTree.build(tokenExtractor.extractTokens(expression));
        return evaluator.evaluate(parseTree, variablePool);
    }
}
