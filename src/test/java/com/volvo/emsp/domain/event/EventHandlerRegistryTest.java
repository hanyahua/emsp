package com.volvo.emsp.domain.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class EventHandlerRegistryTest {

    private EventHandlerRegistry registry;

    private static class TestEvent extends DomainEvent {
        protected TestEvent(EventSource eventSource) {
            super(eventSource);
        }
    }

    private static class SubEvent extends TestEvent {

        protected SubEvent(EventSource eventSource) {
            super(eventSource);
        }
    }

    private static class AnotherTestEvent extends DomainEvent {
        protected AnotherTestEvent(EventSource eventSource) {
            super(eventSource);
        }
    }

    private static class TestEventHandler implements EventHandler<TestEvent> {
        @Override
        public void handle(TestEvent event) {

        }
    }

    private static class AnotherTestEventHandler implements EventHandler<AnotherTestEvent> {
        @Override
        public void handle(AnotherTestEvent event) {

        }
    }

    @BeforeEach
    void setUp() {
        // init handler
        EventHandler<TestEvent> testEventHandler = new TestEventHandler();
        EventHandler<AnotherTestEvent> anotherTestEventHandler = new AnotherTestEventHandler();

        // init EventHandlerRegistry
        registry = new EventHandlerRegistry(List.of(testEventHandler, anotherTestEventHandler));
    }

    @Test
    void testGetHandlerForRegisteredEvent() {
        // test TestEvent
        EventHandler<TestEvent> handler = registry.getHandler(TestEvent.class);
        assertNotNull(handler, "Handler for TestEvent should not be null");
        assertInstanceOf(TestEventHandler.class, handler, "Handler should be an instance of TestEventHandler");

        // test AnotherTestEvent
        EventHandler<AnotherTestEvent> anotherHandler = registry.getHandler(AnotherTestEvent.class);
        assertNotNull(anotherHandler, "Handler for AnotherTestEvent should not be null");
        assertInstanceOf(AnotherTestEventHandler.class, anotherHandler, "Handler should be an instance of AnotherTestEventHandler");
    }

    @Test
    void testGetHandlerForUnregisteredEvent() {
        EventHandler<DomainEvent> handler = registry.getHandler(DomainEvent.class);
        assertNull(handler, "Handler for unregistered event type should be null");
    }

    @Test
    void testEmptyRegistry() {
        EventHandlerRegistry emptyRegistry = new EventHandlerRegistry(List.of());
        EventHandler<TestEvent> handler = emptyRegistry.getHandler(TestEvent.class);
        assertNull(handler, "Handler for TestEvent should be null in an empty registry");
    }


    @Test
    void testDuplicateHandlersRegistration() {
        // twe handler with same event
        @SuppressWarnings("all") // unsupport lambda
        EventHandler<TestEvent> firstHandler = new EventHandler<TestEvent>() {
            @Override
            public void handle(TestEvent domainEvent) {

            }
        };
        @SuppressWarnings("all") // unsupport lambda
        EventHandler<TestEvent> secondHandler = new EventHandler<TestEvent>() {
            @Override
            public void handle(TestEvent domainEvent) {

            }
        };

        // register first
        registry = new EventHandlerRegistry(List.of(firstHandler));
        registry.registerHandler(secondHandler); // register second

        // is new one
        EventHandler<TestEvent> handler = registry.getHandler(TestEvent.class);
        assertEquals(secondHandler, handler, "The second handler should overwrite the first one.");
    }

    @Test
    void testHandlerForSubclassEvent() {
        EventHandler<TestEvent> handler = registry.getHandler(TestEvent.class);
        assertNotNull(handler, "Handler for TestEvent should not be null");

        EventHandler<? extends DomainEvent> handlerForSubclass = registry.getHandler(SubEvent.class);
        assertEquals(handler, handlerForSubclass, "Handler for SubEvent should be the same as for TestEvent");
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        // threads
        int threads = 1000;
        Thread[] threadPool = new Thread[threads];

        // create a handler and register
        EventHandler<TestEvent> testHandler = new TestEventHandler();
        registry = new EventHandlerRegistry(List.of(testHandler));

        // multiple tread
        for (int i = 0; i < threads; i++) {
            threadPool[i] = new Thread(() -> {
                EventHandler<TestEvent> handler = registry.getHandler(TestEvent.class);
                assertNotNull(handler, "Handler should not be null in concurrent access.");
            });
        }

        // start
        for (Thread thread : threadPool) thread.start();
        for (Thread thread : threadPool) thread.join();
    }

    @Test
    void testHandlerForUnrelatedEventType() {
        // UnrelatedEvent
        class UnrelatedEvent extends DomainEvent {
            protected UnrelatedEvent(EventSource eventSource) {
                super(eventSource);
            }
        }

        // get null
        EventHandler<?> handler = registry.getHandler(UnrelatedEvent.class);
        assertNull(handler, "Handler for UnrelatedEvent should be null.");
    }

    @Test
    void testRegisterLambdaHandler() {
        EventHandlerRegistry registry = new EventHandlerRegistry();

        //  Lambda
        EventHandler<TestEvent> handler = domainEvent ->
                System.out.println("Handling TestEvent: " + domainEvent);

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                registry.registerHandler(handler));

        assertTrue(exception.getMessage().contains("lambda"));
    }


}