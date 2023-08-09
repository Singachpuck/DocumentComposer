package com.kpi.composer.service.compose.parse;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kpi.composer.exception.DatasetParseException;
import com.kpi.composer.service.compose.evaluate.Variable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JsonVariableParser implements VariableParser {

    private final ObjectMapper objectMapper;

    @Override
    public Collection<Variable<?>> parse(byte[] jsonBytes) {
        final Collection<Variable<?>> variables = new ArrayList<>();
        try {
            final JsonNode tree = objectMapper.readTree(jsonBytes);
            if (tree.isArray()) {
                throw new DatasetParseException("Array can not be the root of dataset");
            }
            for (Iterator<Map.Entry<String, JsonNode>> it = tree.fields(); it.hasNext(); ) {
                final Map.Entry<String, JsonNode> nodeEntry = it.next();
                final String name = nodeEntry.getKey();
                final JsonNode node = nodeEntry.getValue();
                variables.add(createVariable(name, node));
            }
        } catch (IOException e) {
            throw new DatasetParseException(e.getMessage(), e);
        }
        return variables;
    }
    private Variable<?> createVariable(String name, JsonNode node) throws JsonProcessingException {
        if (node.isValueNode()) {
            if (node.isFloatingPointNumber()) {
                return new Variable<>(name, node.asDouble());
            } else if (node.isIntegralNumber()) {
                return new Variable<>(name, node.asLong());
            } else if (node.isTextual()) {
                return new Variable<>(name, node.asText());
            } else {
                throw new DatasetParseException("Unsupported node type: " + node.getClass().getName() + " + " + node.getNodeType().name());
            }
        } else {
            return new Variable<>(name, objectMapper.writeValueAsString(node));
        }
    }
}
