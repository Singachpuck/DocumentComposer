package com.kpi.composer.service.compose;

import lombok.Getter;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@Getter
public enum Operators {
    PLUS("+", 5),
    MINUS("-", 5),
    MULTIPLY("*", 10),
    DIVIDE("/", 10),
    GROUP_OPEN("(", -1),
    GROUP_CLOSE(")", -1);

    private final String value;

    private final int precedence;

    Operators(String value, int precedence) {
        this.value = value;
        this.precedence = precedence;
    }

    public static Operators ofValue(String value) {
        for (Operators operator : Operators.values()) {
            if (Objects.equals(operator.getValue(), value)) {
                return operator;
            }
        }
        return null;
    }

    public boolean isFunction() {
        return Set.of(PLUS, MINUS, MULTIPLY, DIVIDE).contains(this);
    }
}
