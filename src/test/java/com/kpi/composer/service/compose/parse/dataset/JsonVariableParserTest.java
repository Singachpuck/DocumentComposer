package com.kpi.composer.service.compose.parse.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpi.composer.TestUtil;
import com.kpi.composer.exception.DatasetParseException;
import com.kpi.composer.service.compose.evaluate.Variable;
import com.kpi.composer.service.compose.parse.JsonVariableParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JsonVariableParserTest {

    final ObjectMapper om = new ObjectMapper();

    final JsonVariableParser variableParser = new JsonVariableParser(om);

    @Test
    void extractTest() throws URISyntaxException, IOException {
        final List<Variable<?>> variables = List.of(
                new Variable<>("string1", "string1"),
                new Variable<>("string2", "string2"),
                new Variable<>("number1", 10L),
                new Variable<>("number2", 20L),
                new Variable<>("float1", 10.5),
                new Variable<>("float2", 20.5),
                new Variable<>("object1", "{\"name\":\"value\"}"),
                new Variable<>("array1", "[10,\"dedq\",15.5,16]"));

        final Collection<Variable<?>> extractedVariables = variableParser.parse(TestUtil.readResource("json/testDataset1.json"));
        assertEquals(8, extractedVariables.size());

        for (Variable<?> variable : extractedVariables) {
            final Optional<Variable<?>> target = variables
                    .stream()
                    .filter(v -> v.getName().equals(variable.getName()))
                    .findFirst();
            assertTrue(target.isPresent());
            assertEquals(target.get().getValue(), variable.getValue());
            System.out.print(variable.getName());
            System.out.print(" - ");
            System.out.println(variable.getValue());
        }
    }

    @Test
    void emptyTest() throws URISyntaxException, IOException {
        final Collection<Variable<?>> extractedVariables = variableParser.parse(TestUtil.readResource("json/emptyDataset.json"));
        assertEquals(0, extractedVariables.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"json/wrongDataset1.json", "json/wrongDataset2.json", "json/wrongDataset3.json"})
    void negativeTesting(String file) {
        assertThrows(DatasetParseException.class, () -> variableParser.parse(TestUtil.readResource(file)));
    }

//    private byte[] loadJson(String file) throws URISyntaxException, IOException {
//        URL resource = getClass().getClassLoader().getResource(file);
//        return Files.readAllBytes(Paths.get(resource.toURI()));
//    }

}