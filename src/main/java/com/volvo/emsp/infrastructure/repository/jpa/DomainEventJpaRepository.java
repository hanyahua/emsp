package com.volvo.emsp.infrastructure.repository.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volvo.emsp.domain.event.DomainEvent;
import com.volvo.emsp.domain.event.EventSource;
import com.volvo.emsp.domain.event.enums.EventStatus;
import com.volvo.emsp.domain.repository.DomainEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class DomainEventJpaRepository implements DomainEventRepository {

    private static final Logger log = LoggerFactory.getLogger(DomainEventJpaRepository.class);
    private final SpringDomainEventRepository repository;
    private final ObjectMapper objectMapper;

    public DomainEventJpaRepository(SpringDomainEventRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }


    @Override
    public List<DomainEvent> findUnprocessedOrderByTimestampAsc() {
        return repository.findByStatusOrderByTimestampAsc(EventStatus.PENDING.name())
                .stream()
                .map(this::toDomainEvent)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void save(DomainEvent event) {
        // try to get from repository
        Optional<DomainEventStoreModel> optionalDomainEventModel = repository.findById(event.getEventId());

        DomainEventStoreModel model;
        if (optionalDomainEventModel.isPresent()) {
            // yes
            model = optionalDomainEventModel.get();
        } else {
            // no
            model = new DomainEventStoreModel();
            model.setEventId(event.getEventId());
        }
        model.setEventType(event.getClass().getName());
        EventSource eventSource = event.getEventSource();
        if (eventSource != null) {
            model.setAggregateType(eventSource.aggregateType());
            model.setAggregateId(eventSource.aggregateId());
        }
        model.setTimestamp(event.getTimestamp());
        model.setStatus(event.getStatus().name());
        try {
            model.setPayload(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize event", e);
        }
        repository.save(model);
    }

    public void saveAll(List<DomainEvent> event1) {
        for (DomainEvent event : event1) {
            this.save(event);
        }
    }

    @Override
    public Optional<DomainEvent> findById(String eventId) {
        return repository.findById(eventId).map(this::toDomainEvent);
    }

    private DomainEvent toDomainEvent(DomainEventStoreModel model) {
        try {
            Class<?> eventClass = Class.forName(model.getEventType());
            DomainEvent event = (DomainEvent) objectMapper.readValue(model.getPayload(), eventClass);
            event.setStatus(EventStatus.valueOf(model.getStatus()));
            return event;
        } catch (Exception e) {
            log.error("Failed to convert domain event model to domain event", e);
            return null;
        }
    }
}
