package com.volvo.emsp.domain.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailConverterTest {

    private static final String EMAIL = "test1@example.com";
    private final EmailConverter converter = new EmailConverter();

    @Test
    void testConvertToDatabaseColumn_WithValidEmail() {
        // given
        Email email = new Email(EMAIL);

        // when
        String result = converter.convertToDatabaseColumn(email);

        // then
        assertEquals(EMAIL, result);
    }

    @Test
    void testConvertToDatabaseColumn_WithNull() {
        // when
        String result = converter.convertToDatabaseColumn(null);

        // then
        assertNull(result);
    }

    @Test
    void testConvertToEntityAttribute_WithValidString() {
        // given
        String dbData = EMAIL;

        // when
        Email result = converter.convertToEntityAttribute(dbData);

        // then
        assertEquals(new Email(dbData), result);
    }

    @Test
    void testConvertToEntityAttribute_WithNull() {
        // when
        Email result = converter.convertToEntityAttribute(null);

        // then
        assertNull(result);
    }
}