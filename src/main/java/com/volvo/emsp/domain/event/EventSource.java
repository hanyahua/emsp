package com.volvo.emsp.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;

public record EventSource(String aggregateType, Long aggregateId) {

    @JsonCreator
    public EventSource(@JsonProperty("aggregateType") String aggregateType, @JsonProperty("aggregateId") Long aggregateId) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
    }

    @Override
    @Nonnull
    public String toString() {
        return aggregateType + ":" + aggregateId;
    }
}
