package com.volvo.emsp.domain.service;

public interface DomainEventLockService {

    /**
     * tryLockEvent
     */
    boolean tryLockEvent(String eventId);

    void unlockEvent(String eventId);
}
