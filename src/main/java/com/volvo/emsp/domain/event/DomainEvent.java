package com.volvo.emsp.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.volvo.emsp.domain.event.enums.EventStatus;

import java.time.Instant;
import java.util.UUID;

@SuppressWarnings("unused")
public abstract class DomainEvent {

    private final EventSource eventSource;
    private final String eventId;
    private final Instant timestamp;
    private EventStatus status;
    
    protected DomainEvent(EventSource eventSource) {
        this.eventId = UUID.randomUUID().toString();
        this.eventSource = eventSource;
        this.timestamp = Instant.now();
        this.status = EventStatus.PENDING;
    }

    @JsonCreator
    protected DomainEvent(
            @JsonProperty("eventSource") EventSource eventSource,
            @JsonProperty("eventId") String eventId,
            @JsonProperty("timestamp") Instant timestamp
    ) {
        this.eventSource = eventSource;
        this.eventId = eventId;
        this.timestamp = timestamp;
    }

    public String getEventId() {
        return eventId;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }

    @JsonIgnore
    public boolean isProcessed() {
        return EventStatus.PROCESSED == status;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
}