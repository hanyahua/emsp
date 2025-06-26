package com.volvo.emsp.domain.service.impl;

import com.volvo.emsp.domain.service.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("all")
public class ShortSnowflakeIdGenerator implements IdGenerator {

    private final long epoch = 1735689600000L; // 2025-01-01 UTC

    private final long timestampBits = 44L;    // 44位时间戳 500年不超过js精度
    private final long machineIdBits = 4L;     // 4位机器码，16台机器
    private final long sequenceBits = 5L;      // 5位序列号，每毫秒最多32个ID

    private final long maxMachineId = ~(-1L << machineIdBits); // 15
    private final long maxSequence = ~(-1L << sequenceBits);   // 31

    private final long machineIdShift = sequenceBits;
    private final long timestampShift = sequenceBits + machineIdBits;

    private final long machineId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public ShortSnowflakeIdGenerator(@Value("${app.id}") long machineId) {
        if (machineId < 0 || machineId > maxMachineId) {
            throw new IllegalArgumentException("机器ID必须在0和" + maxMachineId + "之间");
        }
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨异常");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        long delta = timestamp - epoch;

        if (delta < 0 || delta >= (1L << timestampBits)) {
            throw new RuntimeException("时间戳超出可用范围");
        }

        return (delta << timestampShift) | (machineId << machineIdShift) | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    // 简单测试
    public static void main(String[] args) {
        ShortSnowflakeIdGenerator generator = new ShortSnowflakeIdGenerator(3); // 机器ID=3
        final long start = System.currentTimeMillis();
        System.out.println("当前时间戳: " + start);

        // 生成100000个ID
        for (int i = 0; i < 100000; i++) {
            long id = generator.nextId();
            System.out.println((i+1) + ". " + id + " (长度: " + String.valueOf(id).length() + ")");
        }
        System.out.println("时间差值: " + (System.currentTimeMillis() - start) + "ms");
    }
}
