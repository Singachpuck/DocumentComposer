package com.kpi.composer.service.compose.extract;

import com.kpi.composer.exception.TemplateProcessingException;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegExExpressionExtractor extends ExpressionExtractor {

    @Override
    public Iterator<Expression> extract(String text, Placeholder tokenPlaceholder, Placeholder escapePlaceholder) {
        final Pattern expressionPattern = this.composePattern(tokenPlaceholder, escapePlaceholder);
        final Matcher matcher = expressionPattern.matcher(text);
        return new ExpressionIterator(matcher, tokenPlaceholder, escapePlaceholder);
    }

    private Pattern composePattern(Placeholder tokenPlaceholder, Placeholder escapePlaceholder) {
        final String escapeBegin = escapePlaceholder.getBegin();
        final String begin = tokenPlaceholder.getBegin();
        final String end = tokenPlaceholder.getEnd();
        final String escapeEnd = escapePlaceholder.getEnd();
        return Pattern.compile(String.format("(?<escapedBegin>%s)?(?<wrapped>%s(?<expression>.+)%s)(?<escapedEnd>%s)?",
                Pattern.quote(escapeBegin),
                Pattern.quote(begin),
                Pattern.quote(end),
                Pattern.quote(escapeEnd)));
    }

    public class ExpressionIterator implements Iterator<Expression> {

        private final Placeholder tokenPlaceholder;

        private final Placeholder escapePlaceholder;

        private final Matcher matcher;

        public ExpressionIterator(Matcher matcher, Placeholder tokenPlaceholder, Placeholder escapePlaceholder) {
            this.matcher = matcher;
            this.tokenPlaceholder = tokenPlaceholder;
            this.escapePlaceholder = escapePlaceholder;
            matcher.find();
        }

        @Override
        public boolean hasNext() {
            return !matcher.hitEnd();
        }

        @Override
        public Expression next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            boolean isEscaped = matcher.group("escapedBegin") != null
                    && matcher.group("escapeEnd") != null;

            final Expression expression;
            if (isEscaped) {
                final String wrappedExpressionBody = matcher.group("wrapped");
                expression = new Expression(wrappedExpressionBody,
                        matcher.start(),
                        matcher.end(),
                        tokenPlaceholder,
                        isEscaped);
            } else {
                final String expressionBody = matcher.group("expression");
                if (expressionBody == null) {
                    throw new TemplateProcessingException("Expression is not defined");
                }
                expression = new Expression(expressionBody,
                        matcher.start("wrapped"),
                        matcher.end("wrapped"),
                        tokenPlaceholder,
                        isEscaped);
            }
            matcher.find();
            return expression;
        }
    }
}
