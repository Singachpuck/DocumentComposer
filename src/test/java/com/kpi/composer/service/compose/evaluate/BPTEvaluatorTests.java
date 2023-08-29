package com.kpi.composer.service.compose.evaluate;

import com.kpi.composer.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BPTEvaluatorTests {

    private VariablePool variablePool;

    private ConversionService conversionService = TestUtil.conversionServiceMock();

    @BeforeEach
    void setup() {
        variablePool = InMemoryVariablePool.load(TestUtil.variableSet(), conversionService);
    }

    @Test
    void treeTest() {

    }

}