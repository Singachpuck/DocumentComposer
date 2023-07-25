package com.kpi.composer.service.compose.evaluate;

import com.kpi.composer.exception.EvaluationException;
import com.kpi.composer.exception.UnknownTokenTypeException;
import com.kpi.composer.service.compose.parse.template.BinaryParseTree;
import com.kpi.composer.service.compose.parse.template.token.LiteralToken;
import com.kpi.composer.service.compose.parse.template.token.OperatorToken;
import com.kpi.composer.service.compose.parse.template.token.VariableToken;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class BPTEvaluator extends Evaluator<BinaryParseTree> {

    private final VariablePool variablePool;

    public Object evaluate(BinaryParseTree tree) {
        final BinaryParseTree.Node root = tree.extract();
        try {
            return evaluateRecursive(root);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new EvaluationException(e.getMessage(), e);
        }
    }

    private Object evaluateRecursive(BinaryParseTree.Node node) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (node.getToken() instanceof LiteralToken lt) {
            return lt.getValue();
        } else if (node.getToken() instanceof VariableToken vt) {
            return variablePool.lookupLiteral(vt.getVariableName()).getValue();
        } else if (node.getToken() instanceof OperatorToken ot) {
            final Object left = evaluateRecursive(node.getLeft());
            final Object right = evaluateRecursive(node.getRight());
            final Method compute = Evaluator.class.getDeclaredMethod(super.operatorMethodMap.get(ot.getOperator()),
                    left.getClass(),
                    right.getClass());
            compute.setAccessible(true);
            return compute.invoke(this, left, right);
        }

        throw new UnknownTokenTypeException(node.getToken().getClass());
    }
}
