package com.volvo.emsp.infrastructure.service;

import com.volvo.emsp.domain.service.DomainEventLockService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Primary
@ConditionalOnProperty(name = "spring.data.redis.host")
public class DomainEventLockServiceRedisImpl implements DomainEventLockService {

    private static final Logger log = LoggerFactory.getLogger(DomainEventLockServiceRedisImpl.class);
    private static final String LOCK_KEY_PREFIX = "event_lock:";
    private static final long DEFAULT_LOCK_TIMEOUT = 1; // 5 sec

    private final StringRedisTemplate redisTemplate;

    public DomainEventLockServiceRedisImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        log.info("using DomainEventLockServiceRedisImpl for lock event");
    }

    @PostConstruct
    public void init() {
        testRedisConnection();
    }

    @Override
    public boolean tryLockEvent(String eventId) {
        String lockKey = LOCK_KEY_PREFIX + eventId;
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", DEFAULT_LOCK_TIMEOUT, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(acquired)) {
            log.info("lock Event success: {}", eventId);
            return true;
        } else {
            log.warn("Event {} is currently locked", eventId);
            return false;
        }
    }

    @Override
    public void unlockEvent(String eventId) {
        String lockKey = LOCK_KEY_PREFIX + eventId;
        redisTemplate.delete(lockKey);
        log.info("lock Event release: {}", eventId);
    }

    private void testRedisConnection() {
        try {
            redisTemplate.getRequiredConnectionFactory().getConnection().ping();
        } catch (Exception e) {
            log.warn("can not connect redis {}", e.getMessage());
        }
    }

}
