package com.kpi.composer.service.compose.evaluate;

import com.kpi.composer.service.compose.Operators;

import java.util.Map;

public abstract class Evaluator<T> {

    protected static final Map<Operators, String> operatorMethodMap = Map.of(
            Operators.PLUS, "add",
            Operators.MINUS, "subtract",
            Operators.DIVIDE, "divide",
            Operators.MULTIPLY, "multiply"
    );

    public abstract Object evaluate(T from, VariablePool variablePool);

    protected Long add(Long a, Long b) {
        return a + b;
    }

    protected Double add(Long a, Double b) {
        return a + b;
    }

    protected String add(Long a, String b) {
        return a + b;
    }

    protected Double add(Double a, Long b) {
        return a + b;
    }

    protected Double add(Double a, Double b) {
        return a + b;
    }

    protected String add(Double a, String b) {
        return a + b;
    }

    protected String add(String a, String b) {
        return a + b;
    }

    protected String add(String a, Long b) {
        return a + b;
    }

    protected String add(String a, Double b) {
        return a + b;
    }

    ///
    protected Long subtract(Long a, Long b) {
        return a - b;
    }

    protected Double subtract(Long a, Double b) {
        return a - b;
    }

    protected Double subtract(Double a, Long b) {
        return a - b;
    }

    protected Double subtract(Double a, Double b) {
        return a - b;
    }

    ///
    protected Long multiply(Long a, Long b) {
        return a * b;
    }

    protected Double multiply(Long a, Double b) {
        return a * b;
    }

    protected Double multiply(Double a, Long b) {
        return a * b;
    }

    protected Double multiply(Double a, Double b) {
        return a * b;
    }

    ///
    protected Long divide(Long a, Long b) {
        return a / b;
    }

    protected Double divide(Long a, Double b) {
        return a / b;
    }

    protected Double divide(Double a, Long b) {
        return a / b;
    }

    protected Double divide(Double a, Double b) {
        return a / b;
    }
}
