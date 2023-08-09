package com.kpi.composer.service.compose.parse.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpi.composer.service.compose.evaluate.Variable;
import com.kpi.composer.service.compose.parse.JsonVariableParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class JsonVariableParserTest {

    @Test
    void t1() throws URISyntaxException, IOException {
        final ObjectMapper om = new ObjectMapper();
        final JsonVariableParser variableParser = new JsonVariableParser(om);

        Collection<Variable<?>> variables = variableParser.parse(this.loadJson());
        assertEquals(5, variables.size());
        variables.forEach(variable -> System.out.println(variable.getValue()));
    }

    private byte[] loadJson() throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader().getResource("testDataset1.json");
        return Files.readAllBytes(Paths.get(resource.toURI()));
    }

}