package com.kpi.composer.service.compose.extract;

import com.kpi.composer.exception.TemplateProcessingException;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;

// TODO: TESTED
@Component
public class DefaultExpressionExtractor extends ExpressionExtractor {

    @Override
    public Iterator<Expression> extract(String text, Placeholder tokenPlaceholder, Placeholder escapePlaceholder) {
        return new ExpressionIterator(text, tokenPlaceholder, escapePlaceholder);
    }

    private static class ExpressionIterator implements Iterator<Expression> {

        private final String text;

        private final int textLen;

        private final Placeholder tokenPlaceholder;

        private final Placeholder escapePlaceholder;

        private final StringBuilder expressionBuffer = new StringBuilder();

        private int expressionBegin = -1;

        private int expressionEnd = -1;

        private boolean isEscaped;

        private int current = 0;

        public ExpressionIterator(String text, Placeholder tokenPlaceholder, Placeholder escapePlaceholder) {
            this.text = text;
            this.textLen = text.length();
            this.tokenPlaceholder = tokenPlaceholder;
            this.escapePlaceholder = escapePlaceholder;
            this.findNext();
        }

        @Override
        public boolean hasNext() {
            return expressionEnd != -1;
        }

        @Override
        public Expression next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            final Expression expression = new Expression(expressionBuffer.toString(),
                    expressionBegin,
                    expressionEnd,
                    tokenPlaceholder,
                    isEscaped);

            this.findNext();
            return expression;
        }

        private boolean findNext() {
            this.reset();
            boolean isEscapeBegin = false;
            int matchCounter = 0;
            final String tokenPhldrBegin = tokenPlaceholder.getBegin();
            final String tokenPhldrEnd = tokenPlaceholder.getEnd();
            final String escapePhldrBegin = escapePlaceholder.getBegin();
            final String escapePhldrEnd = escapePlaceholder.getEnd();
            while (current < textLen) {
                if (expressionBegin == -1) {
                    final int phldrIndex = text.indexOf(tokenPhldrBegin, current);
                    if (phldrIndex == -1) {
                        current = textLen;
                        break;
                    }

                    current = phldrIndex + tokenPhldrBegin.length();
                    // check if placeholder is escaped
                    if (escapePhldrBegin.length() <= phldrIndex) {
                        final String potentialPhldr = text.substring(phldrIndex - escapePhldrBegin.length(), phldrIndex);
                        if (potentialPhldr.equals(escapePhldrBegin)) {
                            isEscapeBegin = true;
                            expressionBuffer.append(tokenPhldrBegin);
                        }
                    }
                    expressionBegin = isEscapeBegin ? phldrIndex - escapePhldrBegin.length() : phldrIndex;
                } else {
                    final char currentCh = text.charAt(current);
                    expressionBuffer.append(currentCh);
                    if (currentCh == tokenPhldrEnd.charAt(matchCounter++)) {
                        if (matchCounter == tokenPhldrEnd.length()) {
                            boolean isEscapeBeginEnd = false;
                            // check if placeholder is escaped in the end
                            if (isEscapeBegin) {
                                if (current + escapePhldrEnd.length() < textLen) {
                                    final String potentialPhldr = text.substring(current + 1, current + 1 + escapePhldrEnd.length());
                                    if (potentialPhldr.equals(escapePhldrEnd)) {
                                        isEscapeBeginEnd = true;
                                    }
                                }
                            }

                            if (isEscapeBeginEnd) {
                                expressionEnd = current + 1 + escapePhldrEnd.length();
                                isEscaped = true;
                            } else {
                                if (isEscapeBegin) {
                                    expressionBegin += escapePhldrBegin.length();
                                    expressionBuffer.delete(0, tokenPhldrBegin.length());
                                }
                                expressionEnd = current + 1;
                                expressionBuffer.delete(expressionBuffer.length() - tokenPhldrEnd.length(),
                                        expressionBuffer.length());
                            }
                            current++;
                            return true;
                        }
                    } else {
                        matchCounter = 0;
                    }
                    current++;
                }
            }

            return false;
        }

        private void reset() {
            isEscaped = false;
            expressionBegin = -1;
            expressionEnd = -1;
            expressionBuffer.delete(0, expressionBuffer.length());
        }
    }
}
