package com.volvo.emsp.domain.model;

import com.volvo.emsp.testmodel.Emaids;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmaidTest {

    @Test
    void testValidEmaid() {
        // given
        String validEmaid = "CN8VOLSXZGQEN0";

        // when & then
        assertDoesNotThrow(() -> new Emaid(validEmaid));
    }

    @Test
    void testInvalidEmaidFormat() {
        // given - invalid format cases
        String[] invalidEmaids = {
            null,                   // null
            "",                     // empty
            "CN8VO",               // too short
            "CN8VOLSXZGQEN0123",   // too long
            "12345LSXZGQEN0",      // invalid country code
            "CN&&￥LSXZGQEN0",      // invalid provider code
            "CN8VO!SXZGQEN0"       // invalid characters
        };

        // when & then
        for (String invalidEmaid : invalidEmaids) {
            assertThrows(IllegalArgumentException.class,
                () -> new Emaid(invalidEmaid),
                "Should throw exception for invalid EMAID: " + invalidEmaid);
        }
    }

    @Test
    void testEmaidEquality() {
        // given
        String emaidStr = Emaids.CONTRACT_ID1;
        Emaid emaid1 = new Emaid(emaidStr);
        Emaid emaid2 = new Emaid(emaidStr);

        // when & then
        assertEquals(emaid1, emaid2, "Equal EMAIDs should be equal");
        assertEquals(emaidStr, emaid1.toString(), "EMAID toString should return original string");
    }
}