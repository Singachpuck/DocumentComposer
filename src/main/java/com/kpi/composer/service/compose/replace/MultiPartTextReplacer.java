package com.kpi.composer.service.compose.replace;

import com.kpi.composer.exception.ExpressionParseException;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import com.kpi.composer.service.compose.extract.Expression;
import com.kpi.composer.service.compose.extract.ExpressionExtractor;
import com.kpi.composer.service.compose.extract.Placeholder;
import com.kpi.composer.service.compose.parse.ExpressionParser;
import org.springframework.core.convert.ConversionService;

import java.util.Iterator;
import java.util.List;

public abstract class MultiPartTextReplacer<T extends TextHolder> extends TextReplacer {

    public MultiPartTextReplacer(ExpressionExtractor expressionExtractor,
                                 ExpressionParser expressionParser,
                                 ConversionService conversionService) {
        super(expressionExtractor, expressionParser, conversionService);
    }

    public void replaceParts(List<T> texts, Placeholder tokenPholder, Placeholder escapePholder, VariablePool variablePool) {
        if (texts == null || texts.isEmpty()) {
            throw new IllegalArgumentException("Can not replace text for empty text providers.");
        }
        final StringBuilder replacedTextSb = new StringBuilder();
        final int[] edges = new int[texts.size()];
        for (int i = 0; i < texts.size(); i++) {
            final TextHolder item = texts.get(i);
            if (item != null) {
                final String text = item.getText();
                replacedTextSb.append(text);
                edges[i] = replacedTextSb.length();
            }
        }

        final Iterator<Expression> expressionIterator = expressionExtractor.extract(replacedTextSb.toString(),
                tokenPholder, escapePholder);

        int delta = 0;
        int target = 0;
        while (expressionIterator.hasNext()) {
            final Expression expression = expressionIterator.next();

            // TODO: handle isEscaped case
            if (!expression.isEscaped()) {
                final Object parsed = expressionParser.parse(expression.getValue(), variablePool);
                final String replacement = conversionService.convert(parsed, String.class);
                if (replacement == null) {
                    throw new ExpressionParseException("Can not cast parsed object to String");
                }

                final int exprStart = expression.getStart() + delta;
                final int exprEnd = expression.getEnd() + delta;
                while (exprStart > edges[target]) {
                    target++;
                }
                final int currentEdge = target == 0 ? 0 : edges[target - 1];

                final TextHolder holder = texts.get(target);
                final StringBuilder currentText = new StringBuilder(holder.getText());
                currentText.replace(exprStart - currentEdge, exprEnd - currentEdge, replacement);
                holder.setText(currentText.toString(), 0);

                // TODO: figure out how to handle edges.
                //  Possible solution. Try!
                if (target < texts.size() - 1 && edges[target] < exprEnd) {
//                    int i = target + 1;
                    target++;
                    while (edges[target] < exprEnd) {
                        texts.get(target).setText("", 0);
                        target++;
                    }

                    final TextHolder currentHolder = texts.get(target);
                    final String s = currentHolder.getText();
                    final String trimmedText = s.substring(exprEnd - edges[target - 1]);
                    currentHolder.setText(trimmedText, 0);
                }
                final int exprLen = exprEnd - exprStart;
                delta += replacement.length() - exprLen;
                for (int i = target; i < edges.length; i++) {
                    edges[i] += delta;
                }
            }
        }
    }

    private String replaceText(String source, Template template, VariablePool variablePool) {
        final StringBuilder replacedTextSb = new StringBuilder(source);
        final Iterator<Expression> expressionIterator = expressionExtractor.extract(source,
                new Placeholder(template.getBeginTokenPlaceholder(), template.getEndTokenPlaceholder()),
                new Placeholder(template.getBeginEscapePlaceholder(), template.getEndEscapePlaceholder()));
        while (expressionIterator.hasNext()) {
            Expression expression = expressionIterator.next();

            if (!expression.isEscaped()) {
                final Object parsed = expressionParser.parse(expression.getValue(), variablePool);
                final String replacement = conversionService.convert(parsed, String.class);
                if (replacement == null) {
                    throw new ExpressionParseException("Can not cast parsed object to String");
                }
                replacedTextSb.replace(expression.getStart(), expression.getEnd(), replacement);
            }
        }
        return replacedTextSb.toString();
    }
}
