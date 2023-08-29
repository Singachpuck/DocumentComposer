package com.kpi.composer;

import com.kpi.composer.service.compose.evaluate.Variable;
import com.kpi.composer.service.compose.evaluate.VariablePool;
import com.kpi.composer.service.compose.replace.TextHolder;
import org.mockito.Mockito;
import org.springframework.core.convert.ConversionService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.when;

public class TestUtil {

    public static Set<Variable<?>> variableSet() {
        return Set.of(
                new Variable<>("long", 10L),
                new Variable<>("string", "200"),
                new Variable<>("double", 10.5D)
        );
    }


    public static ConversionService conversionServiceMock() {
        final ConversionService mock = Mockito.mock(ConversionService.class);

        when(mock.convert(any(), any())).thenAnswer(invocation -> {
            Object source = invocation.getArgument(0);
            Class<?> desired = invocation.getArgument(1, Class.class);
            if (desired == Long.class) {
                if (source instanceof Long) {
                    return source;
                } else if (source instanceof Double d) {
                    return d.longValue();
                } else if (source instanceof String s) {
                    return Long.valueOf(s);
                }
            } else if (desired == Double.class) {
                if (source instanceof Long l) {
                    return l.doubleValue();
                } else if (source instanceof Double d) {
                    return d;
                } else if (source instanceof String s) {
                    return Double.valueOf(s);
                }
            } else {
                return String.valueOf(source);
            }
            return null;
        });

        return mock;
    }

    public static byte[] readResource(String file) throws URISyntaxException, IOException {
        URL resource = TestUtil.class.getClassLoader().getResource(file);
        return Files.readAllBytes(Paths.get(resource.toURI()));
    }
}
