package com.kpi.composer.service.compose.parse;

import com.kpi.composer.exception.ExpressionParseException;
import com.kpi.composer.service.compose.Operators;
import com.kpi.composer.service.compose.parse.token.LiteralToken;
import com.kpi.composer.service.compose.parse.token.OperatorToken;
import com.kpi.composer.service.compose.parse.token.Token;
import com.kpi.composer.service.compose.parse.token.VariableToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// TODO: TESTED!
public class BinaryParseTree {

    private final List<Token> tokens;

    private Node root;

    private BinaryParseTree(List<Token> tokens) {
        this.tokens = new ArrayList<>(tokens);
        this.verifyBalanced();
    }

    public static BinaryParseTree build(List<Token> tokens) {
        final BinaryParseTree bpt = new BinaryParseTree(tokens);
        bpt.root = bpt.buildRoot(bpt.tokens);
        return bpt;
    }

    public Node extract() {
        return root;
    }

    public Node cloneAndExtract() {
        return SerializationUtils.clone(root);
    }

    private void verifyBalanced() {
        final Stack<OperatorToken> parentheses = new Stack<>();
        for (Token token : tokens) {
            if (token instanceof OperatorToken ot) {
                if (ot.getOperator() == Operators.GROUP_OPEN) {
                    parentheses.push(ot);
                } else if (ot.getOperator() == Operators.GROUP_CLOSE) {
                    if (parentheses.isEmpty()) {
                        throw new ExpressionParseException("')' goes before '('");
                    }
                    parentheses.pop();
                }
            }
        }
        if (!parentheses.isEmpty()) {
            throw new ExpressionParseException("Not all parentheses are closed");
        }
    }

    private Node buildRoot(List<Token> tokens) {
        tokens.add(0, new OperatorToken(Operators.GROUP_OPEN));
        tokens.add(new OperatorToken(Operators.GROUP_CLOSE));
        final Stack<OperatorToken> operators = new Stack<>();
        final Stack<Node> nodeStack = new Stack<>();
        for (Token current : tokens) {
            if (current instanceof LiteralToken || current instanceof VariableToken) {
                final Node leaveNode = new Node();
                leaveNode.setToken(current);
                nodeStack.push(leaveNode);
            } else if (current instanceof OperatorToken ot) {
                if (ot.getOperator() == Operators.GROUP_OPEN) {
                    operators.push(ot);
                } else if (ot.getOperator() == Operators.GROUP_CLOSE) {
                    this.buildGroup(operators, nodeStack);
                } else if (!operators.isEmpty() && operators.peek().getOperator().getPrecedence() >= ot.getOperator().getPrecedence()) {
                    nodeStack.push(this.buildNode(operators, nodeStack));
                    operators.push(ot);
                } else {
                    operators.push(ot);
                }
            }
        }
        if (nodeStack.size() != 1 || operators.size() > 0) {
            throw new ExpressionParseException("Can not resolve expression");
        }
        tokens.remove(0);
        tokens.remove(tokens.size() - 1);
        return nodeStack.pop();
    }

    private Node buildNode(Stack<OperatorToken> operators, Stack<Node> nodeStack) {
        if (operators.isEmpty()) {
            throw new ExpressionParseException("Can not resolve expression");
        }
        final OperatorToken operator = operators.pop();
        if (!operator.getOperator().isFunction()) {
            throw new ExpressionParseException("Can not resolve expression");
        }
        final Node node = new Node();
        node.setToken(operator);
        if (nodeStack.isEmpty()) {
            throw new ExpressionParseException("Can not resolve expression");
        }
        node.setRight(nodeStack.pop());
        if (nodeStack.isEmpty()) {
            throw new ExpressionParseException("Can not resolve expression");
        }
        node.setLeft(nodeStack.pop());
        return node;
    }

    private void buildGroup(Stack<OperatorToken> operators, Stack<Node> nodeStack) {
        while (operators.peek().getOperator() != Operators.GROUP_OPEN) {
            nodeStack.push(this.buildNode(operators, nodeStack));
        }
        operators.pop();
    }

    @Getter
    @Setter
    public static class Node implements Serializable {

        private Token token;

        private Node left;

        private Node right;
    }
}
