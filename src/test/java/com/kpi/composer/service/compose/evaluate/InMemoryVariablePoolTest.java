package com.kpi.composer.service.compose.evaluate;

import com.kpi.composer.TestUtil;
import com.kpi.composer.exception.VariableNotFoundException;
import com.kpi.composer.service.compose.parse.token.LiteralToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InMemoryVariablePoolTest {

    private Collection<Variable<?>> variables;

    @BeforeEach
    void setUp() {
        variables = TestUtil.variableSet();
    }

    @Test
    void emptyTest() {
        final InMemoryVariablePool variablePool = InMemoryVariablePool.empty(null);
        assertThrows(VariableNotFoundException.class, () -> variablePool.lookup("string"));
        assertThrows(VariableNotFoundException.class, () -> variablePool.lookup("double"));
        assertThrows(VariableNotFoundException.class, () -> variablePool.lookup("long"));
    }

    @Test
    void lookupTest() {
        final InMemoryVariablePool variablePool = InMemoryVariablePool.load(variables, null);

        Variable<String> string = variablePool.lookup("string", String.class);
        Variable<Long> longVariable = variablePool.lookup("long", Long.class);
        Variable<Double> doubleVariable = variablePool.lookup("double", Double.class);

        assertEquals("200", string.getValue());
        assertThrows(ClassCastException.class, () -> variablePool.lookup("string", Long.class));
        assertThrows(ClassCastException.class, () -> variablePool.lookup("string", Double.class));

        assertEquals(10L, longVariable.getValue());
        assertThrows(ClassCastException.class, () -> variablePool.lookup("long", Double.class));
        assertThrows(ClassCastException.class, () -> variablePool.lookup("long", String.class));

        assertEquals(10.5D, doubleVariable.getValue());
        assertThrows(ClassCastException.class, () -> variablePool.lookup("double", Long.class));
        assertThrows(ClassCastException.class, () -> variablePool.lookup("double", String.class));
    }

    @Test
    void lookupTestWithConversion() {
        final ConversionService conversionService = TestUtil.conversionServiceMock();
        final InMemoryVariablePool variablePool = InMemoryVariablePool.load(variables, conversionService);

        Variable<String> string = variablePool.lookup("string", String.class);
        Variable<Long> stringLong = variablePool.lookup("string", Long.class);
        Variable<Double> stringDouble = variablePool.lookup("string", Double.class);
        Variable<Long> longVariable = variablePool.lookup("long", Long.class);
        Variable<Double> longDouble = variablePool.lookup("long", Double.class);
        Variable<String> longString = variablePool.lookup("long", String.class);
        Variable<Double> doubleVariable = variablePool.lookup("double", Double.class);
        Variable<Long> doubleLong = variablePool.lookup("double", Long.class);
        Variable<String> doubleString = variablePool.lookup("double", String.class);

        assertEquals("200", string.getValue());
        assertEquals(200L, stringLong.getValue());
        assertEquals(200.0, stringDouble.getValue());
        assertEquals(10L, longVariable.getValue());
        assertEquals(10.0, longDouble.getValue());
        assertEquals("10", longString.getValue());
        assertEquals(10.5D, doubleVariable.getValue());
        assertEquals(10L, doubleLong.getValue());
        assertEquals("10.5", doubleString.getValue());
    }

    @Test
    void untypedLookupTest() {
        final InMemoryVariablePool variablePool = InMemoryVariablePool.load(variables, null);

        Variable<?> string = variablePool.lookup("string");
        Variable<?> integerVariable = variablePool.lookup("long");
        Variable<?> doubleVariable = variablePool.lookup("double");

        assertEquals(10L, integerVariable.getValue());
        assertEquals("200", string.getValue());
        assertEquals(10.5D, doubleVariable.getValue());
    }

    @Test
    void replaceTest() {
        final InMemoryVariablePool variablePool = InMemoryVariablePool.load(variables, null);

        assertNotNull(variablePool.lookup("string"));
        assertNotNull(variablePool.lookup("double"));
        assertNotNull(variablePool.lookup("long"));

        final List<Variable<?>> newVariables = List.of(
                new Variable<>("new1", 10.0D),
                new Variable<>("new2", "new")
        );
        variablePool.replace(newVariables);

        assertThrows(VariableNotFoundException.class, () -> variablePool.lookup("string"));
        assertThrows(VariableNotFoundException.class, () -> variablePool.lookup("double"));
        assertThrows(VariableNotFoundException.class, () -> variablePool.lookup("long"));
        assertNotNull(variablePool.lookup("new1"));
        assertNotNull(variablePool.lookup("new2"));
    }

    @Test
    void literalTest() {
        final ConversionService conversionService = TestUtil.conversionServiceMock();
        final InMemoryVariablePool variablePool = InMemoryVariablePool.load(variables, conversionService);

        LiteralToken<String> string = variablePool.lookupLiteral("string", String.class);
        LiteralToken<Long> stringLong = variablePool.lookupLiteral("string", Long.class);
        LiteralToken<Double> stringDouble = variablePool.lookupLiteral("string", Double.class);
        LiteralToken<Long> longVariable = variablePool.lookupLiteral("long", Long.class);
        LiteralToken<Double> longDouble = variablePool.lookupLiteral("long", Double.class);
        LiteralToken<String> longString = variablePool.lookupLiteral("long", String.class);
        LiteralToken<Double> doubleVariable = variablePool.lookupLiteral("double", Double.class);
        LiteralToken<Long> doubleLong = variablePool.lookupLiteral("double", Long.class);
        LiteralToken<String> doubleString = variablePool.lookupLiteral("double", String.class);

        assertEquals("200", string.getValue());
        assertEquals(200L, stringLong.getValue());
        assertEquals(200.0, stringDouble.getValue());
        assertEquals(10L, longVariable.getValue());
        assertEquals(10.0, longDouble.getValue());
        assertEquals("10", longString.getValue());
        assertEquals(10.5D, doubleVariable.getValue());
        assertEquals(10L, doubleLong.getValue());
        assertEquals("10.5", doubleString.getValue());
    }

    @Test
    void untypedLiteralTest() {
        final InMemoryVariablePool variablePool = InMemoryVariablePool.load(variables, null);

        LiteralToken<?> string = variablePool.lookupLiteral("string");
        LiteralToken<?> integerVariable = variablePool.lookupLiteral("long");
        LiteralToken<?> doubleVariable = variablePool.lookupLiteral("double");

        assertEquals(10L, integerVariable.getValue());
        assertEquals("200", string.getValue());
        assertEquals(10.5D, doubleVariable.getValue());
    }

}