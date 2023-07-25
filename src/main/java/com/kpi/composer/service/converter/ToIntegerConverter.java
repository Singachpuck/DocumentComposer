package com.kpi.composer.service.converter;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.Set;

public class ToIntegerConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(
                new ConvertiblePair(String.class, Integer.class),
                new ConvertiblePair(Double.class, Integer.class)
        );
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (sourceType.getType() == Integer.class) {
            return source;
        } else if (sourceType.getType() == String.class) {
            return Integer.parseInt((String) source);
        } else {
            return ((Double) source).intValue();
        }
    }
}
