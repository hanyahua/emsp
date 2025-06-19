package com.volvo.emsp.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EmaidConverter implements AttributeConverter<Emaid, String> {

    @Override
    public String convertToDatabaseColumn(Emaid attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public Emaid convertToEntityAttribute(String dbData) {
        return dbData != null ? new Emaid(dbData) : null;
    }
}

