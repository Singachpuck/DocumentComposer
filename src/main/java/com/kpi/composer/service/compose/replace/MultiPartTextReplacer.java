package com.kpi.composer.service.compose.replace;

import com.kpi.composer.exception.ExpressionParseException;
import com.kpi.composer.model.entities.Template;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import com.kpi.composer.service.compose.extract.Expression;
import com.kpi.composer.service.compose.extract.ExpressionExtractor;
import com.kpi.composer.service.compose.extract.Placeholder;
import com.kpi.composer.service.compose.parse.ExpressionParser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;

import java.util.Iterator;
import java.util.List;

// TODO: TESTED
@RequiredArgsConstructor
public abstract class MultiPartTextReplacer {

    protected final ExpressionExtractor expressionExtractor;

    protected final ExpressionParser expressionParser;

    protected final ConversionService conversionService;

    public void replaceParts(List<TextHolder> texts, Placeholder tokenPholder, Placeholder escapePholder, VariablePool variablePool) {
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
            final String replacement;
            if (!expression.isEscaped()) {
                final Object parsed = expressionParser.parse(expression.getValue(), variablePool);
                replacement = conversionService.convert(parsed, String.class);
                if (replacement == null) {
                    throw new ExpressionParseException("Can not cast parsed object to String");
                }
            } else {
                replacement = expression.getValue();
            }

            final int exprStart = expression.getStart() + delta;
            final int exprEnd = expression.getEnd() + delta;
            while (exprStart >= edges[target]) {
                target++;
            }
            final int currentEdge = target == 0 ? 0 : edges[target - 1];

            final TextHolder holder = texts.get(target);
            final StringBuilder currentText = new StringBuilder(holder.getText());
            currentText.replace(exprStart - currentEdge, exprEnd - currentEdge, replacement);
            holder.setText(currentText.toString(), 0);

            if (target < texts.size() - 1 && edges[target] < exprEnd) {
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
            final int dif = replacement.length() - exprLen;
            delta += dif;
            for (int i = target; i < edges.length; i++) {
                edges[i] += dif;
            }
        }
    }
}
