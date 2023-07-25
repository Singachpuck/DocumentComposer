package com.kpi.composer.service.compose.parse.template;

import com.kpi.composer.exception.ExpressionParseException;
import com.kpi.composer.exception.UnknownTokenTypeException;
import com.kpi.composer.exception.UnsupportedLiteralType;
import com.kpi.composer.service.compose.Operators;
import com.kpi.composer.service.compose.evaluate.BPTEvaluator;
import com.kpi.composer.service.compose.evaluate.Evaluator;
import com.kpi.composer.service.compose.evaluate.Variable;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import com.kpi.composer.service.compose.parse.template.token.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExpressionParser {

    private final VariablePool variablePool;

    @Autowired
    public ExpressionParser(VariablePool variablePool) {
        this.variablePool = variablePool;
    }

    public Object parse(String expression) {
        final BinaryParseTree parseTree = this.buildParseTree(this.extractTokens(expression));
        final Evaluator<BinaryParseTree> evaluator = new BPTEvaluator(variablePool);
        return evaluator.evaluate(parseTree);
    }

    private BinaryParseTree buildParseTree(List<Token> tokens) {
        return BinaryParseTree.build(tokens);
    }

    private List<Token> extractTokens(String expression) {
        expression = expression.trim();

        final List<Token> tokenList = new ArrayList<>();
        final char[] chars = expression.toCharArray();
        final StringBuilder currentToken = new StringBuilder();
        boolean inVariable = false;
        boolean inLiteral = false;
        boolean inOperator = false;
        boolean isNumberLiteral = false;
        boolean isDecimal = false;
        boolean isEscaped = false;
        for (int i = 0; i < chars.length; i++) {
            final char current = chars[i];

            if (Atom.ALPHA.belongs(current)) {
                if (inOperator) {
                    tokenList.add(this.flushToken(currentToken, OperatorToken.class));
                    inOperator = false;
                }
                if (!inVariable && !inLiteral) {
                    inVariable = true;
                }
                if (inLiteral) {
                    if (isNumberLiteral) {
                        throw new ExpressionParseException("Variable can not start from a digit. Expression: "+ expression + ". Position: " + i);
                    } else if (isEscaped) {
                        throw new ExpressionParseException("Can not escape " + current + " in position " + i + ". Expression: " + expression);
                    }
                }
                currentToken.append(current);
            } else if (Atom.DIGIT.belongs(current)) {
                if (inOperator) {
                    tokenList.add(this.flushToken(currentToken, OperatorToken.class));
                    inOperator = false;
                }
                if (!inVariable && !inLiteral) {
                    inLiteral = true;
                    isNumberLiteral = true;
                }
                if (inLiteral && isEscaped) {
                    throw new ExpressionParseException("Can not escape " + current + " in position " + i + ". Expression: " + expression);
                }
                currentToken.append(current);
            } else if (Atom.SPECIAL.belongs(current)) {
                if (inLiteral) {
                    if (isNumberLiteral) {
                        tokenList.add(this.flushToken(currentToken, LiteralToken.class,
                                this.getLiteralType(isNumberLiteral, isDecimal)));
                        inLiteral = false;
                    } else if (isEscaped) {
                        throw new ExpressionParseException("Can not escape " + current + " in position " + i + ". Expression: " + expression);
                    } else {
                        currentToken.append(current);
                        continue;
                    }
                } else if (inVariable) {
                    tokenList.add(this.flushToken(currentToken, VariableToken.class));
                    inVariable = false;
                }
                if (inOperator) {
                    tokenList.add(this.flushToken(currentToken, OperatorToken.class));
                }
                inOperator = true;
                currentToken.append(current);
            } else if (Atom.QUOTE.belongs(current)) {
                if (inOperator) {
                    tokenList.add(this.flushToken(currentToken, OperatorToken.class));
                    inOperator = false;
                }
                if (inVariable) {
                    throw new ExpressionParseException("Unexpected token " + current + " in position " + i + ". Expression: " + expression);
                }
                if (inLiteral) {
                    if (isNumberLiteral) {
                        throw new ExpressionParseException("Unexpected token " + current + " in position " + i + ". Expression: " + expression);
                    }
                    if (isEscaped) {
                        currentToken.append(current);
                        isEscaped = false;
                    } else {
                        tokenList.add(this.flushToken(currentToken, LiteralToken.class));
                        inLiteral = false;
                    }
                } else {
                    inLiteral = true;
                    isNumberLiteral = false;
                }
            } else if (Atom.SPACE.belongs(current)) {
                if (inOperator) {
                    tokenList.add(this.flushToken(currentToken, OperatorToken.class));
                    inOperator = false;
                }
                if (inVariable) {
                    tokenList.add(this.flushToken(currentToken, VariableToken.class));
                    inVariable = false;
                }
                if (inLiteral) {
                    if (isNumberLiteral) {
                        tokenList.add(this.flushToken(currentToken, LiteralToken.class,
                                this.getLiteralType(isNumberLiteral, isDecimal)));
                        inLiteral = false;
                    } else if (isEscaped) {
                        throw new ExpressionParseException("Unexpected escape character " + current + " in position " + i + ". Expression: " + expression);
                    } else {
                        currentToken.append(current);
                    }
                }
            } else if (Atom.POINT.belongs(current)) {
                if (inLiteral) {
                    if (isNumberLiteral) {
                        if (isDecimal) {
                            throw new ExpressionParseException("Unexpected token " + current + " in position " + i + ". Expression: " + expression);
                        } else {
                            currentToken.append(current);
                            isDecimal = true;
                        }
                    } else if (isEscaped) {
                        throw new ExpressionParseException("Can not escape " + current + " in position " + i + ". Expression: " + expression);
                    } else {
                        currentToken.append(current);
                    }
                    continue;
                }
                throw new ExpressionParseException("Unexpected token " + current + " in position " + i + ". Expression: " + expression);
            } else if (Atom.ESCAPE.belongs(current)) {
                if (!inLiteral || isNumberLiteral) {
                    throw new ExpressionParseException("Unexpected token " + current + " in position " + i + ". Expression: " + expression);
                }
                if (isEscaped) {
                    currentToken.append(current);
                    isEscaped = false;
                } else {
                    isEscaped = true;
                }
            } else {
                if (!inLiteral) {
                    throw new ExpressionParseException("Unexpected token " + current + " in position " + i + ". Expression: " + expression);
                }

                currentToken.append(current);
            }
        }

        if (inVariable) {
            tokenList.add(this.flushToken(currentToken, VariableToken.class));
        } else if (inLiteral) {
            if (!isNumberLiteral) {
                throw new ExpressionParseException("String literal is not closed");
            }
            tokenList.add(this.flushToken(currentToken,
                    LiteralToken.class, this.getLiteralType(isNumberLiteral, isDecimal)));
        } else if (inOperator) {
            tokenList.add(this.flushToken(currentToken, OperatorToken.class));
        }

        return tokenList;
    }

    private Class<?> getLiteralType(boolean isNumberLiteral, boolean isDecimal) {
        if (isNumberLiteral) {
            return isDecimal ? Double.class : Long.class;
        }
        return String.class;
    }

    private <T extends Token> Token flushToken(StringBuilder tokenBuilder, Class<T> type) {
        return this.flushToken(tokenBuilder, type, String.class);
    }

    private <T extends Token> Token flushToken(StringBuilder tokenBuilder, Class<T> type, Class<?> literalType) {
        final Token token;
        if (type == OperatorToken.class) {
            token = new OperatorToken(Operators.ofValue(tokenBuilder.toString()));
        } else if (type == LiteralToken.class) {
            token = new LiteralToken(this.buildTokenValue(tokenBuilder, literalType));
        } else if (type == VariableToken.class) {
            token = new VariableToken(tokenBuilder.toString());
        } else {
            throw new UnknownTokenTypeException(type);
        }

        tokenBuilder.delete(0, tokenBuilder.length());
        return token;
    }

    private Object buildTokenValue(StringBuilder token, Class<?> type) {
        if (type == Long.class) {
            return Long.valueOf(token.toString());
        } else if (type == Double.class) {
            return Double.valueOf(token.toString());
        } else if (type == String.class) {
            return token.toString();
        }

        throw new UnsupportedLiteralType(type);
    }
}
