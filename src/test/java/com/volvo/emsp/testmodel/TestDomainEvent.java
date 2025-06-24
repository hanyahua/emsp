package com.volvo.emsp.testmodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.volvo.emsp.domain.event.DomainEvent;
import com.volvo.emsp.domain.event.EventSource;

import java.time.Instant;

public class TestDomainEvent extends DomainEvent {

    @JsonCreator
    public TestDomainEvent(
            @JsonProperty("eventSource") EventSource eventSource,
            @JsonProperty("eventId") String eventId,
            @JsonProperty("timestamp") Instant timestamp) {
        super(eventSource, eventId, timestamp);
    }

    public TestDomainEvent(EventSource eventSource) {
        super(eventSource);
    }

    public static TestDomainEvent fromSource(EventSource eventSource) {
        return new TestDomainEvent(eventSource);
    }

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String json = """
                {"eventSource":{"aggregateType":"Test","aggregateId":1},"eventId":"62895a85-8116-45c9-b61f-92dc8f7440ee","timestamp":"2025-06-24T04:57:38.772138300Z","status":"PENDING"}
                """;
        DomainEvent event = objectMapper.readValue(json, TestDomainEvent.class);
        System.out.println(event);
    }
}
