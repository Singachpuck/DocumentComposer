package com.kpi.composer.service.compose.parse.token;

import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.function.Predicate;

@RequiredArgsConstructor
public enum Atom {
    ALPHA(Character::isAlphabetic),
    DIGIT(Character::isDigit),
    SPACE(Character::isSpaceChar),
    ESCAPE((atom) -> '\\' == atom),
    QUOTE((atom) -> '\'' == atom),
    SPECIAL((atom) -> Set.of('+', '-', '*', '/', '(', ')').contains(atom)),
    POINT((atom) -> '.' == atom);

    private final Predicate<Character> isAtom;

    public boolean belongs(char c) {
        return isAtom.test(c);
    }
}
