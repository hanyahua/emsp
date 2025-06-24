
package com.volvo.emsp.infrastructure.service.integration;

import com.volvo.emsp.infrastructure.service.DomainEventLockServiceRedisImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DomainEventLockServiceRedisImplTest {

    @Autowired
    private DomainEventLockServiceRedisImpl domainEventLockService;

    private final String eventId = UUID.randomUUID().toString();


    @BeforeEach
    void setUp() {
    }

    @Test
    void testUnlockNonExistentEvent() {
        String nonExistentId = UUID.randomUUID().toString();
        assertDoesNotThrow(() -> domainEventLockService.unlockEvent(nonExistentId));
    }

    @Test
    void testLockTimeout() throws InterruptedException {
        // first tread
        assertTrue(domainEventLockService.tryLockEvent(eventId));

        // another thread
        try (ExecutorService executor = newFixedThreadPool(1)) {
            try {
                Future<Boolean> future = executor.submit(() ->
                        domainEventLockService.tryLockEvent(eventId));
                assertFalse(future.get(5, TimeUnit.SECONDS));
            } catch (ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            } finally {
                executor.shutdown();
            }
        }
    }

    @Test
    void testReentrantLock() {
        // first get lock
        assertTrue(domainEventLockService.tryLockEvent(eventId));

        // get again false
        assertFalse(domainEventLockService.tryLockEvent(eventId));

        // release
        domainEventLockService.unlockEvent(eventId);
        assertTrue(domainEventLockService.tryLockEvent(eventId));
    }

    @Test
    void testMultipleEventsLock() {
        String eventId1 = UUID.randomUUID().toString();
        String eventId2 = UUID.randomUUID().toString();

        assertTrue(domainEventLockService.tryLockEvent(eventId1));
        assertTrue(domainEventLockService.tryLockEvent(eventId2));

        assertFalse(domainEventLockService.tryLockEvent(eventId1));
        assertFalse(domainEventLockService.tryLockEvent(eventId2));
    }

    @Test
    void testConcurrentLockAccess() throws InterruptedException {
        int threadCount = 10;
        AtomicInteger successfulLocks;
        try (ExecutorService executorService = newFixedThreadPool(threadCount)) {
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch completionLatch = new CountDownLatch(threadCount);
            successfulLocks = new AtomicInteger(0);

            // 创建多个线程同时尝试获取锁
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        startLatch.await(); // 等待所有线程就绪
                        if (domainEventLockService.tryLockEvent(eventId)) {
                            successfulLocks.incrementAndGet();
                            // handle time
                            handleTime();
                            domainEventLockService.unlockEvent(eventId);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        completionLatch.countDown();
                    }
                });
            }

            startLatch.countDown();
            completionLatch.await();
            executorService.shutdown();
        }

        // only one can get the lock
        assertEquals(1, successfulLocks.get());
    }

    private static void handleTime() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}