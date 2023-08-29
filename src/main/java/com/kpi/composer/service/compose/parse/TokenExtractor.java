package com.kpi.composer.service.compose.parse;


import com.kpi.composer.service.compose.parse.token.Token;

import java.util.List;

public interface TokenExtractor {

    List<Token> extractTokens(String expression);
}
