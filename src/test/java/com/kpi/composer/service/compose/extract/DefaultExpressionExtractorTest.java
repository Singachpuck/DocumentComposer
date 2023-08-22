package com.kpi.composer.service.compose.extract;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DefaultExpressionExtractorTest {

    private final DefaultExpressionExtractor expressionExtractor = new DefaultExpressionExtractor();

    static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of("Test paragraph. Some random text with placeholder in the middle ${string1}$ which doesn’t really mean anything!! After it comes another placeholder ${string2}$. And the final text.",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\"")),
                Arguments.of("${string1}$ Test paragraph. Some random text with placeholder in the middle ${string3}$ which doesn’t really mean anything!! After it comes another placeholder. And the final text. ${string2}$",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\"")),
                Arguments.of("Test paragraph. Some random text with placeholder in the middle ${string1}$${string3}$ which doesn’t really mean anything!! After it comes another placeholder. And the final text. ${string2}$",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\"")),
                Arguments.of("\"${string1}$\" Test paragraph. Some random text with placeholder in the middle \"${string3}$\" which doesn’t really mean anything!! After it comes another placeholder. And the final text. \"${string2}$\"",
                        new Placeholder("${", "}$"), new Placeholder("\"", "\"")),
                Arguments.of("''${string1}$'' Test paragraph. Some random text with placeholder in the middle ''${string3}$'' which doesn’t really mean anything!! After it comes another placeholder. And the final text. ''${string2}$''",
                        new Placeholder("${", "}$"), new Placeholder("''", "''")),
                Arguments.of("""
                        Природа завжди"${string1}$" вражає своєю величчю та різноманіттям. Вона є джерелом незрівнянної краси та натхнення для всього живого на Землі. Великі простори лісів розсипані пестливою зеленню дерев та кущів. Гори, вкриті сніговим покривом, стоять як вічні сторожі природи. Ріки пливуть своїми мальовничими водами, віддавши в жертву свої береги для життя різних істот.

                        Природа подарувала людині${string2}$ різноманітний рій тварин та рослин, від яких залежить різноманітність та баланс екосистем. Кожна квітка, кожен листок має свою власну історію, а кожна тварина відіграє свою роль у складному танці життя.

                        Атмосфера повна чарівності та таємничості, в якій${string3}$ кожен подих вітру несе аромат квітів, а ранкова роса на травах виглядає як перлинні краплини надії. Сонячні промені гріють і зігрівають все живе, даруючи світло та тепло.

                        Але природа вразлива, і потребує нашої дбайливої уваги та підтримки.${string4}$ Важливо зберігати різноманіття видів, зберігати водні ресурси, здорові ліси та чисте повітря. Турбуватися про навколишнє середовище — це забезпечення нашого власного майбутнього та майбутнього нашої планети.

                        ${string5}$Природа — це скарбниця див і таємниць, яку ми повинні берегти і шанувати, передаючи цю красу майбутнім поколінням.
                        """, new Placeholder("${", "}$"), new Placeholder("\"", "\""))
        );
    }

    @ParameterizedTest
    @MethodSource("args")
    void extract(String text, Placeholder tokenPlaceholder, Placeholder escapePlaceholder) {
        final Iterator<Expression> expressionIterator = expressionExtractor.extract(text, tokenPlaceholder, escapePlaceholder);

        while (expressionIterator.hasNext()) {
            final Expression expression = expressionIterator.next();

            System.out.print(text.substring(expression.getStart(), expression.getEnd()));
            System.out.print(" ");
            System.out.println(expression.getValue());
        }
    }
}