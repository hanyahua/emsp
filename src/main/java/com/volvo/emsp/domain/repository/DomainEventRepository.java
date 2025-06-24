package com.volvo.emsp.domain.repository;

import com.volvo.emsp.domain.event.DomainEvent;

import java.util.List;
import java.util.Optional;

public interface DomainEventRepository {

    List<DomainEvent> findUnprocessedOrderByTimestampAsc();

    void save(DomainEvent event);

    Optional<DomainEvent> findById(String eventId);
}