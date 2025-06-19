package com.volvo.emsp.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmaidSnowflakeGeneratorTest {

    private EmaidSnowflakeGenerator emaidGenerator;
    private static final int MACHINE_ID = 0;

    @BeforeEach
    void setUp() {
        emaidGenerator = new EmaidSnowflakeGenerator(MACHINE_ID);
    }

    @Test
    void testGenerateEmaid_Uniqueness() {
        // given
        int numberOfEmaids = 100000;
        Set<String> generatedEmaids = new HashSet<>();

        // when
        for (int i = 0; i < numberOfEmaids; i++) {
            String emaid = emaidGenerator.generateEmaid();
            generatedEmaids.add(emaid);
        }

        // then
        assertEquals(numberOfEmaids, generatedEmaids.size(),
                "All generated EMAIDs should be unique");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000})
    void testConstructor_InvalidMachineId(int invalidMachineId) {
        // then
        assertThrows(IllegalArgumentException.class,
                () -> new EmaidSnowflakeGenerator(invalidMachineId),
                "Should throw IllegalArgumentException for invalid machine ID");
    }


    @Test
    void testGenerateEmaid_CounterOverflow() {
        // given
        int maxCounter = 1296; // 36^2
        Set<String> generatedEmaids = new HashSet<>();

        // when
        for (int i = 0; i < maxCounter + 10; i++) {
            String emaid = emaidGenerator.generateEmaid();
            generatedEmaids.add(emaid);
        }
        // then
        assertTrue(generatedEmaids.size() > maxCounter,
                "Should handle counter overflow by using timestamp and random chars");
    }

}