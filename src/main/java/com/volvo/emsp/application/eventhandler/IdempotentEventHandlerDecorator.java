package com.volvo.emsp.application.eventhandler;

import com.volvo.emsp.domain.event.DomainEvent;
import com.volvo.emsp.domain.event.EventHandler;
import com.volvo.emsp.domain.event.enums.EventStatus;
import com.volvo.emsp.domain.repository.DomainEventRepository;
import com.volvo.emsp.domain.service.DomainEventLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;


public abstract class IdempotentEventHandlerDecorator<T extends DomainEvent> implements EventHandler<T> {

    private static final Logger log = LoggerFactory.getLogger(IdempotentEventHandlerDecorator.class);

    private final DomainEventLockService lockService;
    private final DomainEventRepository eventRepository;

    public IdempotentEventHandlerDecorator(
            DomainEventLockService lockService,
            DomainEventRepository eventRepository) {
        this.lockService = lockService;
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional
    public void handle(T event) {
        log.info("handling event: {}:{}", event.getClass().getTypeName(), event.getEventId());
        // 1. check is the event already been processed
        if (isEventProcessed(event)) {
            log.info("event has already been processed: {}", event.getEventId());
            return;
        }

        // 2. try lock
        try {
            if (!lockService.tryLockEvent(event.getEventId())) {
                log.info("event is processing by other handler or thread: {}", event.getEventId());
                return;
            }

            // 3. double check
            if (isEventProcessed(event)) {
                log.info("event has already been processed: {}", event.getEventId());
                return;
            }

            // 4. handle event
            doHandle(event);

            // 5. mark processed
            markEventAsProcessed(event);

        } finally {
            // 6. release lock
            lockService.unlockEvent(event.getEventId());
        }
    }

    protected abstract void doHandle(T event);

    private boolean isEventProcessed(T event) {
        return eventRepository.findById(event.getEventId())
                .map(DomainEvent::isProcessed)
                .orElse(false);
    }

    @SuppressWarnings("unused")
    private String generateLockKey(T event) {
        return String.format("event:lock:%s:%s",
                event.getClass().getSimpleName(),
                event.getEventId());
    }

    private void markEventAsProcessed(T event) {
        event.setStatus(EventStatus.PROCESSED);
        eventRepository.save(event);
    }
}