package com.volvo.emsp.domain.service.impl;

import com.volvo.emsp.domain.service.EmaidGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 *  this is a demo, you can use other method
 */

@Component
public class EmaidSnowflakeGenerator implements EmaidGenerator {

    private static final String COUNTRY_CODE = "CN";
    private static final String SERVICE_CODE = "8VO";
    private final Snowflake46 snowflake46;

    public EmaidSnowflakeGenerator(@Value("${app.id}") int machineId) {
        if (machineId < 0 || machineId > 7) {
            throw new IllegalArgumentException("Invalid machine ID, Machine ID must be 0-7.");
        }
        this.snowflake46 = new Snowflake46(machineId);
    }

    public String generateEmaid() {
        return COUNTRY_CODE +
                SERVICE_CODE +
                snowflake46.nextIdBase36();
    }


    @SuppressWarnings("all")
    static class Snowflake46 {

        // 起始时间（2025-01-01 00:00:00 UTC）
        private final long epoch = 1735689600000L;

        // 位分配
        private final long timestampBits = 40L;  // 可用34.8年
        private final long machineIdBits = 3L;   // 8台机器
        private final long sequenceBits = 3L;    // 每毫秒8个ID

        // 最大值计算
        private final long maxMachineId = ~(-1L << machineIdBits);
        private final long maxSequence = ~(-1L << sequenceBits);

        // 移位计算
        private final long machineIdShift = sequenceBits;
        private final long timestampShift = sequenceBits + machineIdBits;

        private final long machineId;
        private long sequence = 0L;
        private long lastTimestamp = -1L;

        // 36进制字符表
        private static final char[] BASE36_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

        public Snowflake46(long machineId) {
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

            return ((timestamp - epoch) << timestampShift)
                    | (machineId << machineIdShift)
                    | sequence;
        }

        public String nextIdBase36() {
            long id = nextId();
            char[] buf = new char[9];
            Arrays.fill(buf, '0');

            int pos = 8;
            do {
                buf[pos--] = BASE36_CHARS[(int)(id % 36)];
                id /= 36;
            } while (id > 0 && pos >= 0);

            return new String(buf);
        }

        private long waitNextMillis(long lastTimestamp) {
            long timestamp = System.currentTimeMillis();
            while (timestamp <= lastTimestamp) {
                timestamp = System.currentTimeMillis();
            }
            return timestamp;
        }

        public static void main(String[] args) {
            // 测试示例
            Snowflake46 generator = new Snowflake46(1); // 机器ID=1

            System.out.println("起始时间戳: " + generator.epoch);
            final long start = System.currentTimeMillis();
            System.out.println("当前时间戳: " + start);

            // 生成10个ID
            for (int i = 0; i < 100000; i++) {
                String id = generator.nextIdBase36();
                System.out.println((i+1) + ". " + id + " (长度: " + id.length() + ")");
            }

            System.out.println("时间差值: " + (System.currentTimeMillis() - start) + "ms");
        }
    }
}