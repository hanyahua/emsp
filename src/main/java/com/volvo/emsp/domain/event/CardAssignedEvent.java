package com.volvo.emsp.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class CardAssignedEvent extends DomainEvent {

    private final Long cardId;
    private final Long accountId;
    
    public CardAssignedEvent(EventSource eventSource, Long cardId, Long accountId) {
        super(eventSource);
        this.cardId = cardId;
        this.accountId = accountId;
    }

    @JsonCreator
    public CardAssignedEvent(
            @JsonProperty("eventSource") EventSource eventSource,
            @JsonProperty("eventId") String eventId,
            @JsonProperty("timestamp") Instant timestamp,
            @JsonProperty("cardId") Long cardId,
            @JsonProperty("accountId") Long accountId) {
        super(eventSource, eventId, timestamp);
        this.cardId = cardId;
        this.accountId = accountId;
    }

    public Long getCardId() {
        return cardId;
    }

    public Long getAccountId() {
        return accountId;
    }
}