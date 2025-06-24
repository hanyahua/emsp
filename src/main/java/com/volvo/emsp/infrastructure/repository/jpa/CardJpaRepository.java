package com.volvo.emsp.infrastructure.repository.jpa;

import com.volvo.emsp.domain.model.Card;
import com.volvo.emsp.domain.repository.CardRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class CardJpaRepository implements CardRepository {

    private final SpringCardRepository springCardRepository;

    public CardJpaRepository(SpringCardRepository cardRepository) {
        this.springCardRepository = cardRepository;
    }

    @Override
    public Optional<Card> findById(Long cardId) {
        return springCardRepository.findById(cardId);
    }

    @Override
    public Page<Card> findByLastUpdatedBetween(@Nullable LocalDateTime from, @Nullable LocalDateTime to, Pageable pageable) {
        return springCardRepository.findByLastUpdatedBetween(from, to, pageable);
    }

    @Override
    public Card save(Card card) {
        return springCardRepository.save(card);
    }

    @Override
    public boolean existsByRfidUid(String rfidUid) {
        return springCardRepository.existsByRfidUid(rfidUid);
    }

    @Override
    public boolean existsByVisibleNumber(String visibleNumber) {
        return springCardRepository.existsByVisibleNumber(visibleNumber);
    }

    public Optional<Card> findByRfidUid(String rfidUid) {
        return springCardRepository.findByRfidUid(rfidUid);
    }
}
