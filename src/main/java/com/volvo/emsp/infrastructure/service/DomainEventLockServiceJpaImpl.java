package com.volvo.emsp.infrastructure.service;

import com.volvo.emsp.domain.service.DomainEventLockService;
import com.volvo.emsp.infrastructure.repository.jpa.DomainEventStoreModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@ConditionalOnMissingBean(DomainEventLockServiceRedisImpl.class)
public class DomainEventLockServiceJpaImpl implements DomainEventLockService {

    private static final Logger log = LoggerFactory.getLogger(DomainEventLockServiceJpaImpl.class);
    private final EntityManager entityManager;
    private static final int DEFAULT_LOCK_TIMEOUT = 1000;

    public DomainEventLockServiceJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        log.info("using DomainEventLockServiceJpaImpl for lock event");
    }

    @Override
    public boolean tryLockEvent(String eventId) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            log.error("tryLockEvent must be called within a transaction context");
            throw new IllegalStateException("No active transaction found when trying to lock event");
        }

        try {
            List<DomainEventStoreModel> eventModels = entityManager
                    .createQuery("SELECT e FROM " +
                            " DomainEventStoreModel e " +
                            " WHERE e.eventId = :eventId", DomainEventStoreModel.class)
                    .setParameter("eventId", eventId)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setHint("jakarta.persistence.lock.timeout", DEFAULT_LOCK_TIMEOUT) // 1 second
                    .getResultList();
            if (eventModels.isEmpty()) {
                log.warn("Event {} not found or already locked", eventId);
                return false;
            }
            log.info("Event {} is locked", eventId);
            return true;
        } catch (PessimisticLockException | LockTimeoutException e) {
            log.warn("Event {} is currently locked by another transaction", eventId);
            return false;
        }
    }

    @Override
    public void unlockEvent(String eventId) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            log.error("unlockEvent must be called within a transaction context");
            throw new IllegalStateException("No active transaction found when trying to unlock event");
        }
        log.info("lock will be release when transaction commited");
    }

}
