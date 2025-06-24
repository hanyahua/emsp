package com.volvo.emsp.domain.service;

import com.volvo.emsp.domain.event.DomainEvent;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}