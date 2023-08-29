package com.kpi.composer.service.compose.extract;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;

class DefaultExpressionExtractorTest {

    private final DefaultExpressionExtractor expressionExtractor = new DefaultExpressionExtractor();

    @Test
    void noSuchElementTest() {
        final String s = "Some text without placeholder.";
        final Placeholder tokenPhldr = new Placeholder("${", "}$");
        final Placeholder escapePhldr = new Placeholder("\"", "\"");
        Iterator<Expression> extracted = expressionExtractor.extract(s, tokenPhldr, escapePhldr);
        assertFalse(extracted.hasNext());
        assertThrows(NoSuchElementException.class, extracted::next);
    }

    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of("Test paragraph. Some random text with placeholder in the middle ${target1}$ which doesn’t really mean anything!! After it comes another placeholder ${target2}$. And the final text.${",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\""), 2),
                Arguments.of("${target1}$ Test paragraph. Some random text with placeholder in the middle ${target2}$ which doesn’t really mean anything!! After it comes another placeholder. And the final text. ${target3}$",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\""), 3),
                Arguments.of("Test paragraph. Some random text with placeholder in the middle ${target1}$${target2}$ which doesn’t really mean anything!! After it comes another placeholder. And the final text. ${target3}$${target4}$${target5}$",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\""), 5),
                Arguments.of("\"${target1}$\" Test paragraph. Some random text with placeholder in the middle ${target2}$\" which doesn’t really mean anything!! After it comes another placeholder. And the final text. \"${target3}$\"",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\""), 1),
                Arguments.of("\"${target1}$\" Test paragraph. Some random text with placeholder in the middle \"${target2}$\" which doesn’t really mean anything!! After it comes another placeholder. And the final text. \"${target3}$\"",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\""), 0),
                Arguments.of("\"${target1}$\" Test paragraph. Some random text with placeholder in the middle \"${target2}$ which doesn’t really mean anything!! After it comes another placeholder. And the final text. \"${target3}$",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\""), 2),
                Arguments.of("''${target1}$'' Test paragraph. Some random text with placeholder in the middle ${target2}$'' which doesn’t really mean anything!! After it comes another placeholder. And the final text. ''${target3}$''",
                        new Placeholder("${", "}$"), new Placeholder("''", "''"), 1),
                Arguments.of("""
                        Природа завжди"${target1}$" вражає своєю величчю та різноманіттям. Вона є джерелом незрівнянної краси та натхнення для всього живого на Землі. Великі простори лісів розсипані пестливою зеленню дерев та кущів. Гори, вкриті сніговим покривом, стоять як вічні сторожі природи. Ріки пливуть своїми мальовничими водами, віддавши в жертву свої береги для життя різних істот.

                        Природа подарувала людині${target2}$ різноманітний рій тварин та рослин, від яких залежить різноманітність та баланс екосистем. Кожна квітка, кожен листок має свою власну історію, а кожна тварина відіграє свою роль у складному танці життя.

                        Атмосфера повна чарівності та таємничості, в якій${target3}$ кожен подих вітру несе аромат квітів, а ранкова роса на травах виглядає як перлинні краплини надії. Сонячні промені гріють і зігрівають все живе, даруючи світло та тепло.

                        Але природа вразлива, і потребує нашої дбайливої уваги та підтримки.${target4}$ Важливо зберігати різноманіття видів, зберігати водні ресурси, здорові ліси та чисте повітря. Турбуватися про навколишнє середовище — це забезпечення нашого власного майбутнього та майбутнього нашої планети.

                        ${target5}$Природа — це скарбниця див і таємниць, яку ми повинні берегти і шанувати, передаючи цю красу майбутнім поколінням.
                        """, new Placeholder("${", "}$"), new Placeholder("\"", "\""), 4)
        );
    }

    @ParameterizedTest
    @MethodSource("args")
    void extractExpressionTest(String text, Placeholder tokenPlaceholder, Placeholder escapePlaceholder, int n) {
        final Iterator<Expression> expressionIterator = expressionExtractor.extract(text, tokenPlaceholder, escapePlaceholder);

        int i = 0;
        int j = 0;
        while (expressionIterator.hasNext()) {
            final Expression e = expressionIterator.next();
            i++;
            if (e.isEscaped()) {
                final String expected = tokenPlaceholder.getBegin() + "target" + i + tokenPlaceholder.getEnd();
                assertEquals(expected, e.getValue());
                assertEquals(escapePlaceholder.getBegin() + expected + escapePlaceholder.getEnd(), text.substring(e.getStart(), e.getEnd()));
            } else {
                j++;
                final String expected = "target" + i;
                assertEquals(expected, e.getValue());
                assertEquals(tokenPlaceholder.getBegin() + expected + tokenPlaceholder.getEnd(), text.substring(e.getStart(), e.getEnd()));
            }
            System.out.println(e.getValue());
        }

        assertEquals(n, j);
    }

    static Stream<Arguments> complexExprArgs() {
        return Stream.of(
                Arguments.of("Test paragraph. Some random text with placeholder in the middle ${target1 + target2}$ which doesn’t really mean anything!! After it comes another placeholder ${target2 / target3 + (target4 * target5)}$. And the final text.",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\""), 2),
                Arguments.of("${   (target3 - target4 * ((100.5) + (40))) / target1 * target2    }$ Test paragraph. Some random text with placeholder in the middle which doesn’t really mean anything!! After it comes another placeholder. And the final text.",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\""), 1),
                Arguments.of("Test paragraph. Some random text with placeholder in the middle ${target1 + target2         }$${        target2 / target3}$ which doesn’t really mean anything!! After it comes another placeholder. And the final text.",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\""), 2),
                Arguments.of("\"${target1 + target2}$\" Test paragraph.",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\""), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("complexExprArgs")
    void extractComplex(String text, Placeholder tokenPlaceholder, Placeholder escapePlaceholder, int n) {
        final Iterator<Expression> expressionIterator = expressionExtractor.extract(text, tokenPlaceholder, escapePlaceholder);

        int j = 0;
        while (expressionIterator.hasNext()) {
            final Expression e = expressionIterator.next();
            if (e.isEscaped()) {
                assertEquals(escapePlaceholder.getBegin() + e.getValue() + escapePlaceholder.getEnd(), text.substring(e.getStart(), e.getEnd()));
            } else {
                j++;
                assertEquals(tokenPlaceholder.getBegin() + e.getValue() + tokenPlaceholder.getEnd(), text.substring(e.getStart(), e.getEnd()));
            }
            MatcherAssert.assertThat(e.getValue(), anyOf(containsString("+"),
                    containsString("-"),
                    containsString("/"),
                    containsString("*")));
            Matcher matcher = Pattern.compile("target\\d").matcher(e.getValue());
            int i = 0;
            while (matcher.find()) {
                i++;
            }
            assertTrue(i >= 2);

            System.out.println(e.getValue());
        }

        assertEquals(n, j);
    }
}