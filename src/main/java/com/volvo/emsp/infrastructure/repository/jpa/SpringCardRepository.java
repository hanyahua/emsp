package com.volvo.emsp.infrastructure.repository.jpa;

import com.volvo.emsp.domain.model.Card;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SpringCardRepository extends JpaRepository<Card, Long> {

    @Query("""
        SELECT c FROM Card c
        WHERE (:from IS NULL OR c.lastUpdated >= :from)
          AND (:to IS NULL OR c.lastUpdated <= :to)
    """)
    Page<Card> findByLastUpdatedBetween(@Nullable LocalDateTime from, @Nullable LocalDateTime to, Pageable pageable);

    Optional<Card> findByRfidUid(String rfidUid);

    boolean existsByRfidUid(String rfidUid);

    boolean existsByVisibleNumber(String visibleNumber);
}
