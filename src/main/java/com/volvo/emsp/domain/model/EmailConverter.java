package com.volvo.emsp.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EmailConverter implements AttributeConverter<Email, String> {

    @Override
    public String convertToDatabaseColumn(Email attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public Email convertToEntityAttribute(String dbData) {
        return dbData != null ? new Email(dbData) : null;
    }
}

