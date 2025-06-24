package com.volvo.emsp.application.schedule;

import com.volvo.emsp.domain.event.DomainEvent;
import com.volvo.emsp.domain.event.EventHandler;
import com.volvo.emsp.domain.event.EventHandlerRegistry;
import com.volvo.emsp.domain.repository.DomainEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class EventProcessingScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventProcessingScheduler.class);

    private final DomainEventRepository eventRepository;
    private final EventHandlerRegistry eventHandlerRegistry;
    private EventProcessingScheduler selfProxy;

    public EventProcessingScheduler(
            DomainEventRepository eventRepository,
            EventHandlerRegistry eventHandlerRegistry) {
        this.eventRepository = eventRepository;
        this.eventHandlerRegistry = eventHandlerRegistry;
    }

    @Autowired
    @Lazy
    public void setSelfProxy(EventProcessingScheduler selfProxy) {
        this.selfProxy = selfProxy;
    }

    @Scheduled(fixedDelay = 5000) //
    public void processEvents() {
        List<DomainEvent> unprocessedEvents =
            eventRepository.findUnprocessedOrderByTimestampAsc();
        for (DomainEvent event : unprocessedEvents) {
            try {
                // processEvent
                if (isReadyForScheduler(event)) {
                    selfProxy.processEvent(event);
                }
            } catch (Exception e) {
                log.error("Failed to process event: {}", event.getEventId(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public void processEvent(DomainEvent event) {
        // Get the handler for the event type (e.g., OrderCreatedEvent
        EventHandler<?> handler = eventHandlerRegistry.getHandler(event.getClass());
        log.info("Processing event: {}", event.getEventId());
        if (handler != null) {
            ((EventHandler<DomainEvent>) handler).handle(event);
        } else  {
            log.warn("No handler found for event type: {}", event.getClass());
        }
    }

    private static boolean isReadyForScheduler(DomainEvent event) {
        return !event.isProcessed() &&
                Instant.now().isAfter(event.getTimestamp().minusSeconds(60)); // 60 seconds delay wait for spring event handler
    }
}