package com.volvo.emsp.domain.event;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class EventHandlerRegistry {

    private final Map<Class<? extends DomainEvent>, EventHandler<? extends DomainEvent>> eventHandlers = new HashMap<>();

    public EventHandlerRegistry() {}

    public EventHandlerRegistry(List<EventHandler<?>> handlers) {
        for (EventHandler<?> handler : handlers) {
            registerHandler(handler);
        }
    }

    @Autowired
    public void registerHandlers(List<EventHandler<?>> handlers) {
        for (EventHandler<?> handler : handlers) {
            _registerHandler(handler);
        }
    }

    public void registerHandler(EventHandler<?> handler) {
        _registerHandler(handler);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends DomainEvent> EventHandler<T> getHandler(Class<T> eventType) {
        EventHandler<? extends DomainEvent> handler = eventHandlers.get(eventType);
        if (handler != null) {
            return (EventHandler<T>) handler;
        }
        // find Parent class Handler
        EventHandler<? extends DomainEvent> parentHandler = findHandlerForParent(eventType);
        if (parentHandler != null) {
            return (EventHandler<T>) parentHandler;
        }
        return (EventHandler<T>) eventHandlers.get(eventType);
    }

    private EventHandler<? extends DomainEvent> findHandlerForParent(Class<?> eventType) {
        Class<?> currentType = eventType.getSuperclass();
        while (currentType != null && DomainEvent.class.isAssignableFrom(currentType)) {
            // try to get Parent event handler
            EventHandler<? extends DomainEvent> handler = eventHandlers.get(currentType);
            if (handler != null) {
                return handler;
            }
            currentType = currentType.getSuperclass(); // 继续向上查找
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T extends DomainEvent>  void _registerHandler(EventHandler<T> handler) {
        Objects.requireNonNull(handler, "handler must not be null");
        if (isLambda(handler)) {
            throw new IllegalArgumentException("can not register a lambda, use named class or unnamed class");
        }
        Class<T> eventType = (Class<T>) getEventTypeUseResolvableType(handler);
        eventHandlers.put(eventType, handler);
    }

    @SuppressWarnings("unused")
    private Class<?> getEventType(EventHandler<?> handler) {
        Class<?> handlerClass = handler.getClass();
        while (handlerClass != null) {
            for (Type type : handlerClass.getGenericInterfaces()) {
                if (type instanceof ParameterizedType pt) {
                    if (pt.getRawType().equals(EventHandler.class)) {
                        return (Class<?>) pt.getActualTypeArguments()[0];
                    }
                }
            }
            handlerClass = handlerClass.getSuperclass();
        }
        throw new IllegalArgumentException("Unable to determine event type for handler: " + handler);
    }

    private Class<?> getEventTypeUseResolvableType(EventHandler<?> handler) {

        // use ResolvableType
        ResolvableType type = ResolvableType.forClass(handler.getClass());
        Class<?> eventType = type.as(EventHandler.class).getGeneric(0).resolve();
        if (eventType == null) {
            throw new IllegalArgumentException("Unable to determine event type for handler: " + handler.getClass());
        }
        return eventType;
    }

    private boolean isLambda(Object handler) {
        return handler.getClass().isSynthetic();
    }
}