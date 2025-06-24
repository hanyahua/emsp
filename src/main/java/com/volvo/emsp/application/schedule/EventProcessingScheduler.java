package com.volvo.emsp.application.schedule;

import com.volvo.emsp.domain.event.DomainEvent;
import com.volvo.emsp.domain.event.EventHandler;
import com.volvo.emsp.domain.repository.DomainEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventProcessingScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventProcessingScheduler.class);

    private final DomainEventRepository eventRepository;
    private final Map<String, EventHandler<? extends DomainEvent>> eventHandlers;
    private EventProcessingScheduler selfProxy;

    public EventProcessingScheduler(DomainEventRepository eventRepository) {
        this.eventRepository = eventRepository;
        eventHandlers = new HashMap<>();
    }

    @Autowired
    public void registerHandlers(List<EventHandler<?>> handlers) {
        for (EventHandler<?> handler : handlers) {
            registerHandler(handler);
        }
    }

    @Autowired
    @Lazy
    public void setSelfProxy(EventProcessingScheduler selfProxy) {
        this.selfProxy = selfProxy;
    }

    private void registerHandler(EventHandler<?> handler) {
        String eventType = getEventType(handler);
        eventHandlers.put(eventType, handler);
    }

    private String getEventType(EventHandler<?> handler) {
        // get class
        Class<?> handlerClass = handler.getClass();
        while (handlerClass != null) {
            // get interface
            for (Type type : handlerClass.getGenericInterfaces()) {
                if (type instanceof ParameterizedType pt) {
                    if (pt.getRawType().equals(EventHandler.class)) {
                        Type eventType = pt.getActualTypeArguments()[0];
                        return eventType.getTypeName();
                    }
                }
            }
            // get father class
            handlerClass = handlerClass.getSuperclass();
        }
        throw new IllegalArgumentException("Unable to determine event type for handler: " + handler
                + ". Make sure it implements EventHandler<T> interface directly.");
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
        EventHandler<?> handler = eventHandlers.get(event.getClass().getTypeName());
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