package com.kpi.composer.service.compose.evaluate;

import com.kpi.composer.exception.EvaluationException;
import com.kpi.composer.exception.UnknownTokenTypeException;
import com.kpi.composer.service.compose.parse.BinaryParseTree;
import com.kpi.composer.service.compose.parse.token.LiteralToken;
import com.kpi.composer.service.compose.parse.token.OperatorToken;
import com.kpi.composer.service.compose.parse.token.VariableToken;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class BPTEvaluator extends Evaluator<BinaryParseTree> {

    public Object evaluate(BinaryParseTree tree, VariablePool variablePool) {
        final BinaryParseTree.Node root = tree.extract();
        try {
            return evaluateRecursive(root, variablePool);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new EvaluationException(e.getMessage(), e);
        }
    }

    private Object evaluateRecursive(BinaryParseTree.Node node, VariablePool variablePool) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (node.getToken() instanceof LiteralToken lt) {
            return lt.getValue();
        } else if (node.getToken() instanceof VariableToken vt) {
            return variablePool.lookupLiteral(vt.getVariableName()).getValue();
        } else if (node.getToken() instanceof OperatorToken ot) {
            final Object left = evaluateRecursive(node.getLeft(), variablePool);
            final Object right = evaluateRecursive(node.getRight(), variablePool);
            final Method compute = Evaluator.class.getDeclaredMethod(Evaluator.operatorMethodMap.get(ot.getOperator()),
                    left.getClass(),
                    right.getClass());
            compute.setAccessible(true);
            return compute.invoke(this, left, right);
        }

        throw new UnknownTokenTypeException(node.getToken().getClass());
    }
}
