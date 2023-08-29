package com.kpi.composer.service.compose.parse.token;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.function.Predicate;

@AllArgsConstructor
public enum Atom {
    ALPHA {
        @Override
        public boolean belongs(char c) {
            return Character.isAlphabetic(c);
        }
    },
    DIGIT {
        @Override
        public boolean belongs(char c) {
            return Character.isDigit(c);
        }
    },
    SPACE {
        @Override
        public boolean belongs(char c) {
            return Character.isSpaceChar(c);
        }
    },
    ESCAPE {
        @Override
        public boolean belongs(char c) {
            return '\\' == c;
        }
    },
    QUOTE {
        @Override
        public boolean belongs(char c) {
            return '\'' == c;
        }
    },
    SPECIAL {
        @Override
        public boolean belongs(char c) {
            return specialChars.contains(c);
        }
    },
    POINT {
        @Override
        public boolean belongs(char c) {
            return '.' == c;
        }
    };

    private static final Set<Character> specialChars = Set.of('+', '-', '*', '/', '(', ')');

    public abstract boolean belongs(char c);
}
