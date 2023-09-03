package com.kpi.composer.service.compose.replace;

import com.kpi.composer.TestUtil;
import com.kpi.composer.service.compose.evaluate.BPTEvaluator;
import com.kpi.composer.service.compose.evaluate.InMemoryVariablePool;
import com.kpi.composer.service.compose.evaluate.Variable;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import com.kpi.composer.service.compose.extract.DefaultExpressionExtractor;
import com.kpi.composer.service.compose.extract.ExpressionExtractor;
import com.kpi.composer.service.compose.extract.Placeholder;
import com.kpi.composer.service.compose.parse.ExpressionParser;
import com.kpi.composer.service.compose.parse.SimpleTokenExtractor;
import com.kpi.composer.service.compose.parse.TokenExtractor;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DocxTextReplacerTest {

    private final ExpressionExtractor expressionExtractor = new DefaultExpressionExtractor();

    private final TokenExtractor tokenExtractor = new SimpleTokenExtractor();

    private final BPTEvaluator evaluator = new BPTEvaluator();

    private final ExpressionParser expressionParser = new ExpressionParser(tokenExtractor, evaluator);

    private final ConversionService conversionService = TestUtil.conversionServiceMock();

    private final MultiPartTextReplacer textReplacer = new MultiPartTextReplacer(expressionExtractor, expressionParser, conversionService) {};

    private VariablePool variablePool;

    private List<Variable<?>> variables;

    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(
                        List.of("Test paragraph. Some random text with placeholder in the middle ${string1} which doesn’t really mean anything!! After it comes another placeholder ${string2}. And the final text."),
                        new Placeholder("${", "}"),
                        new Placeholder("\"", "\""),
                        0
                ),
                Arguments.of(
                        List.of("Test paragraph. Some random text with placeholder in the middle ${string1} which doesn’t really mean anything!!", "After it comes another placeholder ${string2}. And the final text."),
                        new Placeholder("${", "}"),
                        new Placeholder("\"", "\""),
                        0
                ),
                Arguments.of(
                        List.of("Test paragraph. $", "{", "string1", "} !!", "After it comes another placeholder ${string2}. And the final text."),
                        new Placeholder("${", "}"),
                        new Placeholder("\"", "\""),
                        0
                ),
                Arguments.of(
                        List.of("Test paragraph. $", "{", "string1", "} !!", "After it comes another placeholder $", "{", "float1", "}", ". And the final text."),
                        new Placeholder("${", "}"),
                        new Placeholder("\"", "\""),
                        0
                ),
                Arguments.of(
                        List.of("Test paragraph. $", "{", "str", "ing1", "} !!", "After it \"{comes}\" another placeholder $", "{", "flo", "at1", "}", ". And the final text."),
                        new Placeholder("{", "}"),
                        new Placeholder("\"", "\""),
                        1
                ),
                Arguments.of(
                        List.of("Test paragraph. $", "{{{{", "str", "ing1", "}}}} !!", "After it '''{{{{comes}}}}''' another placeholder $", "{{{{", "flo", "at1", "}}}}", ". And the final text."),
                        new Placeholder("{{{{", "}}}}"),
                        new Placeholder("'''", "'''"),
                        1
                ),
                Arguments.of(
                        List.of("{string1}", "{string2}", "{float1}", "{float2}Here as well.", "{number1}", " {number2}", "{array1}", "Some text.{object1}"),
                        new Placeholder("{", "}"),
                        new Placeholder("'", "'"),
                        0
                ),
                Arguments.of(
                        List.of("{", "s", "t", "r", "i", "n", "g", "1", "}","{", "str", "ing2", "}", "{", "flo", "at1", "}", "{", "flo", "at2", "}", "{", "num", "ber1}", "{num", "ber2}", "{array1", "}", "{", "obj", "ect1}"),
                        new Placeholder("{", "}"),
                        new Placeholder("'", "'"),
                        0
                )
        );
    }

    @BeforeEach
    void setup() {
        this.variables = List.of(
                new Variable<>("string1", "replaced_string1"),
                new Variable<>("string2", "replaced_string2"),
                new Variable<>("number1", 10L),
                new Variable<>("number2", 20L),
                new Variable<>("float1", 10.5),
                new Variable<>("float2", 20.5),
                new Variable<>("object1", "{\"name\": \"value\"}"),
                new Variable<>("array1", "[10, \"dedq\", 15.5, 16]"));
        this.variablePool = InMemoryVariablePool.load(variables, conversionService);
    }


    @ParameterizedTest()
    @MethodSource("args")
    void replacementTest(List<String> strings, Placeholder tokenPhldr, Placeholder escapePhldr, int nEscaped) {
        final StringBuilder sb = new StringBuilder();
        strings.forEach(sb::append);
        final String oldText = sb.toString();

        final List<TextHolder> texts = strings.stream()
                .map(text -> (TextHolder) new InMemoryTextHolder(text))
                .toList();


        textReplacer.replaceParts(texts, tokenPhldr, escapePhldr, variablePool);

        sb.delete(0, sb.length());
        texts.forEach(textHolder -> {
            final String currentText = textHolder.getText();
            sb.append(currentText);
            assertFalse(currentText.contains(escapePhldr.getBegin()));
            assertFalse(currentText.contains(escapePhldr.getEnd()));

            if (!currentText.isBlank())
                System.out.println(currentText);
        });

        final String newText = sb.toString();

        int delta = 0;
        for (Variable<?> variable : variables) {
            if (oldText.contains(variable.getName())) {
                delta += variable.getValue().toString().length() - (tokenPhldr.getBegin().length() + tokenPhldr.getEnd().length() + variable.getName().length());
                assertTrue(newText.contains(variable.getValue().toString()));
            }
        }
        assertEquals(delta, newText.length() - oldText.length() + (escapePhldr.getBegin().length() + escapePhldr.getEnd().length()) * nEscaped);
    }

    @AllArgsConstructor
    static class InMemoryTextHolder implements TextHolder {

        private String string;

        @Override
        public String getText() {
            return string;
        }

        @Override
        public void setText(String text, int pos) {
            this.string = text;
        }
    }

}