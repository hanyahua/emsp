package com.volvo.emsp.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SnowflakeIdGeneratorTest {

    private SnowflakeIdGenerator idGenerator;
    private static final int MACHINE_ID = 0;
    private static final int DATACENTER_ID = 0;

    @BeforeEach
    void setUp() {
        idGenerator = new SnowflakeIdGenerator(MACHINE_ID, DATACENTER_ID);
    }

    @Test
    void testGenerateEmaid_Uniqueness() {
        // given
        int numberOfIds = 100000;
        Set<Long> generatedIds = new HashSet<>();

        // when
        for (int i = 0; i < numberOfIds; i++) {
            Long id = idGenerator.nextId();
            generatedIds.add(id);
        }

        // then
        assertEquals(numberOfIds, generatedIds.size(),
                "All generated EMAIDs should be unique");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 100, 1000, 10000, 100000, 1000000, 10000000, 100000})
    void testConstructor_InvalidMachineId(int invalidMachineId) {
        // then
        assertThrows(IllegalArgumentException.class,
                () -> new SnowflakeIdGenerator(invalidMachineId, invalidMachineId),
                "Should throw IllegalArgumentException for invalid machine ID");
    }


    @Test
    void testGenerateCounterOverflow() {
        // given
        int maxCounter = 1296; // 36^2
        Set<Long> ids = new HashSet<>();

        // when
        for (int i = 0; i < maxCounter + 10; i++) {
            Long id = idGenerator.nextId();
            ids.add(id);
        }
        // then
        assertTrue(ids.size() > maxCounter,
                "Should handle counter overflow by using timestamp and random chars");
    }

}