package com.volvo.emsp.application.eventhandler;

import com.volvo.emsp.domain.event.CardAssignedEvent;
import com.volvo.emsp.domain.event.EventHandler;
import com.volvo.emsp.domain.model.Account;
import com.volvo.emsp.domain.repository.AccountRepository;
import com.volvo.emsp.domain.repository.DomainEventRepository;
import com.volvo.emsp.domain.service.DomainEventLockService;
import com.volvo.emsp.domain.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CardAssignmentEventHandler extends IdempotentEventHandlerDecorator<CardAssignedEvent>
        implements EventHandler<CardAssignedEvent> {

    private static final Logger log = LoggerFactory.getLogger(CardAssignmentEventHandler.class);

    private final EmailService emailService;
    private final AccountRepository accountRepository;

    public CardAssignmentEventHandler(
            DomainEventRepository eventRepository,
            DomainEventLockService domainEventLockService,
            EmailService emailService,
            AccountRepository accountRepository) {
        super(domainEventLockService, eventRepository);
        this.emailService = emailService;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public void doHandle(CardAssignedEvent event) {
        processEvent(event);
    }

    /**
     * spring event handle
     * @param event CardAssignedEvent
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onApplicationEvent(CardAssignedEvent event) {
        try {
            this.handle(event);
        } catch (Exception e) {
            log.error("Failed to process event: {}", event.getEventId(), e); // running in async, so handle exception itself
        }
    }

    private void processEvent(CardAssignedEvent event) {
        Account account = accountRepository.findById(event.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        emailService.sendEmail(
                account.getEmail().toString(),
                "Card assigned notification",
                String.format("Card（ID：%d）has be assigned to your account。", event.getCardId())
        );
    }
}