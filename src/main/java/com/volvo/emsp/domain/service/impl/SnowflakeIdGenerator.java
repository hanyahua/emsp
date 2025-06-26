package com.volvo.emsp.domain.service.impl;

import com.volvo.emsp.domain.service.IdGenerator;
import org.springframework.beans.factory.annotation.Value;

@SuppressWarnings("unused")
public class SnowflakeIdGenerator implements IdGenerator {

    // start from (2025-05-01)
    private final static long EPOCH = 1743561600000L;

    private final static long WORKER_ID_BITS = 5L;
    private final static long DATACENTER_ID_BITS = 5L;
    private final static long SEQUENCE_BITS = 12L;

    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private final static long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private final static long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    private final static long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(@Value("${app.id}") long workerId, @Value("${app.datacenterId:0}") long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenterId can't be greater than %d or less than 0", MAX_DATACENTER_ID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}
