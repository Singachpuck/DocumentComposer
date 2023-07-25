package com.kpi.composer.service.converter;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.Set;

public class ToStringConverter implements GenericConverter {
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(
            new ConvertiblePair(Integer.class, String.class),
            new ConvertiblePair(Double.class, String.class)
        );
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        return String.valueOf(source);
    }
}
