package com.kpi.composer.service.converter;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.Set;

public class ToDoubleConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(
                new ConvertiblePair(Integer.class, Double.class),
                new ConvertiblePair(String.class, Double.class)
        );
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

        if (sourceType.getType() == Double.class) {
            return source;
        }

        if (sourceType.getType() == Integer.class) {
            return ((Integer) source).doubleValue();
        } else {
            return Double.parseDouble((String) source);
        }
    }
}
