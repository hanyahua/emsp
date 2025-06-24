package com.volvo.emsp.domain.event;

public interface EventHandler <T extends DomainEvent> {

    void handle(T domainEvent);
}
