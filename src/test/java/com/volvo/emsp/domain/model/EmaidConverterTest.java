package com.volvo.emsp.domain.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmaidConverterTest {

    public static final String EAMID = "CN8VOLSXZGQEN0";
    private final EmaidConverter converter = new EmaidConverter();

    @Test
    void testConvertToDatabaseColumn_WithValidEmaid() {
        // given
        Emaid emaid = new Emaid(EAMID);
        // when
        String result = converter.convertToDatabaseColumn(emaid);
        // then
        assertEquals(EAMID, result);
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
        String dbData = EAMID;
        // when
        Emaid result = converter.convertToEntityAttribute(dbData);
        // then
        assertEquals(new Emaid(dbData), result);
    }

    @Test
    void testConvertToEntityAttribute_WithNull() {
        // when
        Emaid result = converter.convertToEntityAttribute(null);
        // then
        assertNull(result);
    }
}