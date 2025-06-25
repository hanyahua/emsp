package com.volvo.emsp.infrastructure.repository.jpa.integration;

import com.volvo.emsp.BaseDataJpaIntegrationTest;
import com.volvo.emsp.config.JacksonConfig;
import com.volvo.emsp.domain.event.CardAssignedEvent;
import com.volvo.emsp.domain.event.DomainEvent;
import com.volvo.emsp.domain.event.EventSource;
import com.volvo.emsp.domain.event.enums.EventStatus;
import com.volvo.emsp.infrastructure.repository.jpa.DomainEventJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



@Import({JpaConfig.class, DomainEventJpaRepository.class, JacksonConfig.class})
class DomainEventJapRepositoryTest extends BaseDataJpaIntegrationTest {

    @Autowired
    private DomainEventJpaRepository repository;

    @Test
    void testSaveAndFindEvent() {
        // test data
        DomainEvent event = createTestEvent(1L, 1L);
        // save event
        repository.save(event);

        // find
        var foundEvent = repository.findById(event.getEventId());
        assertTrue(foundEvent.isPresent());
        assertEquals(event.getEventId(), foundEvent.get().getEventId());
        assertEquals(EventStatus.PENDING, foundEvent.get().getStatus());
    }

    @Test
    void testFindUnprocessedEvents() {
        // test data
        DomainEvent event1 = createTestEvent(1L, 1L);
        DomainEvent event2 = createTestEvent(2L, 2L);
        DomainEvent event3 = createTestEvent(3L, 3L);
        repository.saveAll(List.of(event1, event2, event3));


        // find
        List<DomainEvent> unprocessedEvents = repository.findUnprocessedOrderByTimestampAsc();

        assertEquals(3, unprocessedEvents.size());
        assertEquals(EventStatus.PENDING, unprocessedEvents.get(0).getStatus());
        assertEquals(EventStatus.PENDING, unprocessedEvents.get(1).getStatus());
        assertEquals(EventStatus.PENDING, unprocessedEvents.get(2).getStatus());
    }

    @Test
    void testUpdateEventStatus() {
        // test data
        DomainEvent event = createTestEvent(1L, 1L);
        repository.save(event);

        // update status
        event.setStatus(EventStatus.PROCESSED);
        repository.save(event);

        // validate
        var updatedEvent = repository.findById(event.getEventId());
        assertTrue(updatedEvent.isPresent());
        assertTrue(updatedEvent.get().isProcessed());
    }

    private DomainEvent createTestEvent(Long cardId, Long userId) {
        return new CardAssignedEvent(new EventSource("Card", 1L), cardId, userId);
    }

}