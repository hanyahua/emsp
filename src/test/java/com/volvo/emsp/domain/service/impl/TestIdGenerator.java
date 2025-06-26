package com.volvo.emsp.domain.service.impl;

import com.volvo.emsp.domain.service.IdGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public class TestIdGenerator implements IdGenerator {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private final AtomicLong sequence = new AtomicLong(1);

    @Override
    public long nextId() {
        String timeComponent = LocalDateTime.now().format(TIME_FORMATTER);
        String sequenceComponent = String.format("%05d", sequence.getAndIncrement() % 10000);
        return Long.parseLong(timeComponent + sequenceComponent);
    }

    public static void main(String[] args) {
        TestIdGenerator idGenerator = new TestIdGenerator();
        for (int i = 0; i < 100; i++) {
            System.out.println(idGenerator.nextId());
        }
    }
}
