package com.volvo.emsp.domain.repository;

import com.volvo.emsp.domain.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CardRepository {

    Optional<Card> findById(Long cardId);

    Page<Card> findByLastUpdatedBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Card save(Card card);

    boolean existsByRfidUid(String rfidUid);

    boolean existsByVisibleNumber(String visibleNumber);
}
