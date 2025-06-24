package com.volvo.emsp.domain.model;

import com.volvo.emsp.domain.event.DomainEvent;
import com.volvo.emsp.domain.event.EventSource;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class AggregateRoot {

    protected abstract Long getId();

    @Transient
    protected final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void addDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    public List<DomainEvent> getDomainEvents() {
        return domainEvents;
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    protected EventSource eventSource() {
        return new EventSource(this.getClass().getSimpleName(), this.getId());
    }
}
