package com.volvo.emsp.infrastructure.repository.jpa.integration;

import com.volvo.emsp.BaseDataJpaIntegrationTest;
import com.volvo.emsp.domain.model.Card;
import com.volvo.emsp.domain.service.IdGenerator;
import com.volvo.emsp.domain.service.impl.TestIdGenerator;
import com.volvo.emsp.infrastructure.repository.jpa.CardJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Import({JpaConfig.class, CardJpaRepository.class}) // custom repository need this
public class CardJpaRepositoryTest extends BaseDataJpaIntegrationTest {

    @Autowired
    private CardJpaRepository cardJapRepository;
    private final IdGenerator idGenerator = new TestIdGenerator();

    private static final List<String[]> cards = Arrays.asList(
            new String[]  {"rfidUid0001", "0001"},
            new String[]  {"rfidUid0002", "0002"},
            new String[]  {"rfidUid0003", "0003"},
            new String[]  {"rfidUid0004", "0004"},
            new String[]  {"rfidUid0005", "0005"}
    );

    @BeforeEach
    void init () {
        for (String[] cardValue : cards) {
            Card card = newCard(cardValue[0], cardValue[1]);
            cardJapRepository.save(card);
        }
    }

    @Test
    void testSave() {
        String rfidUid = "rfidUid0010";
        String visibleNumber = "0010";
        Card newCard = newCard(rfidUid, visibleNumber);
        cardJapRepository.save(newCard);
        Optional<Card> optionalCard = cardJapRepository.findByRfidUid(rfidUid);

        // assert
        assertTrue(optionalCard.isPresent(), "card should exist");
        Card card = optionalCard.get();
        assertEquals(rfidUid, card.getRfidUid(), "rfidUid should same");
    }

    @Test
    void findByLastUpdatedTime() {
        Page<Card> accounts = cardJapRepository.findByLastUpdatedBetween(
                LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
                LocalDateTime.of(LocalDate.now(), LocalTime.MAX),
                Pageable.ofSize(10)
        );

        // assert
        assertEquals(cards.size(), accounts.getTotalElements(), "query count not right");
    }

    @Test
    void findById() {
        String rfidUid = "rfidUid0010";
        String visibleNumber = "0010";
        Card newCard = newCard(rfidUid, visibleNumber);
        Card card = cardJapRepository.save(newCard);
        Optional<Card> optionalCard = cardJapRepository.findById(card.getCardId());

        // assert
        assertTrue(optionalCard.isPresent(), "card should exist");
        Card savedAccount = optionalCard.get();
        assertEquals(newCard.getCardId(), savedAccount.getCardId(), "id should same");
    }

    private Card newCard(String rfidUid, String visibleNumber) {
        return new Card(idGenerator.nextId(), rfidUid, visibleNumber);
    }
}

