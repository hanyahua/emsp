package com.volvo.emsp.application.service;

import com.volvo.emsp.application.command.CreateCardCommand;
import com.volvo.emsp.application.dto.CardDTO;
import com.volvo.emsp.domain.model.Account;
import com.volvo.emsp.domain.model.Card;
import com.volvo.emsp.domain.repository.AccountRepository;
import com.volvo.emsp.domain.repository.CardRepository;
import com.volvo.emsp.domain.service.DomainEventPublisher;
import com.volvo.emsp.domain.service.IdGenerator;
import com.volvo.emsp.execption.BadRequestException;
import com.volvo.emsp.execption.ResourceAlreadyExistsException;
import com.volvo.emsp.execption.ResourceNotFoundException;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class CardApplicationService {

    private static final Logger log = LoggerFactory.getLogger(CardApplicationService.class);
    private final IdGenerator idGenerator;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final DomainEventPublisher eventPublisher;


    public CardApplicationService(
            IdGenerator idGenerator,
            CardRepository cardRepository,
            AccountRepository accountRepository,
            DomainEventPublisher eventPublisher) {
        this.idGenerator = idGenerator;
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CardDTO createCard(CreateCardCommand createCardCommand) {
        log.info("Creating card  {}", createCardCommand);
        if (createCardCommand == null) {
            throw new BadRequestException("Create card command must not be null");
        }
        if (cardRepository.existsByRfidUid(createCardCommand.getRfidUid())) {
            throw new ResourceAlreadyExistsException("Card with RFID UID already exists: " + createCardCommand.getRfidUid());
        }
        if (cardRepository.existsByVisibleNumber(createCardCommand.getVisibleNumber())) {
            throw new ResourceAlreadyExistsException("Card with visible number already exists: " + createCardCommand.getVisibleNumber());
        }
        Card card = new Card(idGenerator.nextId(), createCardCommand.getRfidUid(), createCardCommand.getVisibleNumber());
        card = cardRepository.save(card);
        return CardDTO.of(card);
    }

    @Transactional
    public void activeCard(Long cardId) {
        log.info("Activating card {}", cardId);
        Card card = checkCardExistsAndReturn(cardId);
        card.activate();
        cardRepository.save(card);
    }

    @Transactional
    public void deactivateCard(Long cardId) {
        log.info("Deactivating card {}", cardId);
        Card card = checkCardExistsAndReturn(cardId);
        card.deactivate();
        cardRepository.save(card);
    }

    @Transactional
    public void assignCardToAccount(Long cardId, Long accountId) {
        log.info("Assigning card {} to account {}", cardId, accountId);
        if (cardId == null) {
            throw new BadRequestException("Card ID must not be null");
        }
        if (accountId == null) {
            throw new BadRequestException("Account ID must not be null");
        }
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (optionalCard.isEmpty()) {
            throw new ResourceNotFoundException("Card not found: " + cardId);
        }
        Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if (optionalAccount.isEmpty()) {
            throw new ResourceNotFoundException("Account not found: " + accountId);
        }
        Card card = optionalCard.get();
        Account account = optionalAccount.get();
        card.assignTo(account);
        cardRepository.save(card);

        card.getDomainEvents().forEach(eventPublisher::publish);
        card.clearDomainEvents();
    }


    @Transactional(readOnly = true)
    public Page<CardDTO> findCards(@Nullable OffsetDateTime from, @Nullable OffsetDateTime to, Pageable pageable) {
        Page<Card> cards = cardRepository.findByLastUpdatedBetween(from, to, pageable);
        return cards.map(CardDTO::of);
    }

    @Transactional(readOnly = true)
    public Optional<CardDTO> findCardById(Long id) {
        Optional<Card> optionalCard = cardRepository.findById(id);
        return optionalCard.map(CardDTO::of);
    }

    private Card checkCardExistsAndReturn(Long cardId) {
        if (cardId == null) {
            throw new BadRequestException("Card ID must not be null");
        }
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        if (optionalCard.isEmpty()) {
            throw new ResourceNotFoundException("Card not found: " + cardId);
        }
        return optionalCard.get();
    }
}
