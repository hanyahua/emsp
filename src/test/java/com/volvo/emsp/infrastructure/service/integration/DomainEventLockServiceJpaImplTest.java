
package com.volvo.emsp.infrastructure.service.integration;

import com.volvo.emsp.BaseIntegrationTest;
import com.volvo.emsp.domain.event.DomainEvent;
import com.volvo.emsp.domain.event.EventSource;
import com.volvo.emsp.domain.repository.DomainEventRepository;
import com.volvo.emsp.infrastructure.service.DomainEventLockServiceJpaImpl;
import com.volvo.emsp.testmodel.TestDomainEvent;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.junit.jupiter.api.Assertions.*;

public class DomainEventLockServiceJpaImplTest extends BaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    private DomainEventLockServiceJpaImpl domainEventLockService;

    @Autowired
    private DomainEventRepository domainEventRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private String eventId;


    @BeforeEach
    void setUp() {
        domainEventLockService = new DomainEventLockServiceJpaImpl(entityManager);
        DomainEvent domainEvent = TestDomainEvent.fromSource(new EventSource("Test", 1L));
        eventId  = domainEvent.getEventId();
        domainEventRepository.save(domainEvent);
        domainEventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("event not found"));
    }

    @Test
    void testAcquireLockNotInTransactional() {
        // lock an event
        assertThrows(IllegalStateException.class, () -> domainEventLockService.tryLockEvent(eventId));
    }

    @Test
    @Transactional
    void testAcquireAndReleaseLock() {
        String notExistsId = UUID.randomUUID().toString();

        // get lock from en not exists event
        assertFalse(domainEventLockService.tryLockEvent(notExistsId));

        // lock an event
        assertTrue(domainEventLockService.tryLockEvent(eventId));

        // lock the same event in one transaction
        assertTrue(domainEventLockService.tryLockEvent(eventId));
    }

    @Test
    void testLockAcrossTransactions() {
        // lock from a transaction
        new TransactionTemplate(transactionManager).execute(status -> {
            assertTrue(domainEventLockService.tryLockEvent(eventId));
            return null;
        });

        // lock from another transaction
        new TransactionTemplate(transactionManager).execute(status -> {
            assertTrue(domainEventLockService.tryLockEvent(eventId));
            return null;
        });
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
                        new TransactionTemplate(transactionManager).execute(status -> {
                            if (domainEventLockService.tryLockEvent(eventId)) {
                                successfulLocks.incrementAndGet();
                                // handle time
                                handleTime();
                                domainEventLockService.unlockEvent(eventId);
                            }
                            return null;
                        });
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