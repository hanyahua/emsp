package com.volvo.emsp.infrastructure.service;

import com.volvo.emsp.domain.event.DomainEvent;
import com.volvo.emsp.domain.service.DomainEventPublisher;
import com.volvo.emsp.infrastructure.repository.jpa.DomainEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PersistentDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PersistentDomainEventPublisher.class);
    
    private final DomainEventJpaRepository eventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;


    public PersistentDomainEventPublisher(
            DomainEventJpaRepository eventRepository,
            ApplicationEventPublisher applicationEventPublisher) {
        this.eventRepository = eventRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void publish(DomainEvent event) {
        // save event
        eventRepository.save(event);

        // Spring event
        try {
            applicationEventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.warn(
                    "Failed to publish spring event: {}:{}, but event is persisted",
                    event.getClass().getSimpleName(),
                    event.getEventSource(),
                    e
            );
        }
        log.info("Domain event published: {}:{}",
            event.getClass().getTypeName(),
            event.getEventSource()
        );
    }
}