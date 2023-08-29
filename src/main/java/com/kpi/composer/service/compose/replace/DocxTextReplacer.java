package com.kpi.composer.service.compose.replace;

import com.kpi.composer.service.compose.extract.ExpressionExtractor;
import com.kpi.composer.service.compose.parse.ExpressionParser;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
public class DocxTextReplacer extends MultiPartTextReplacer {

    @Autowired
    public DocxTextReplacer(ExpressionExtractor expressionExtractor,
                            ExpressionParser expressionParser,
                            ConversionService conversionService) {
        super(expressionExtractor, expressionParser, conversionService);
    }

    @RequiredArgsConstructor
    public static class DocxTextHolder implements TextHolder {

        private final XWPFRun run;

        @Override
        public String getText() {
            return run.getText(0);
        }

        @Override
        public void setText(String text, int pos) {
            run.setText(text, pos);
        }
    }
}
