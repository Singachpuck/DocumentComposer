package com.kpi.composer.service.compose.parse;

import com.kpi.composer.service.compose.evaluate.Evaluator;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
