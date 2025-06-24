package com.volvo.emsp.domain.model;

import com.volvo.emsp.domain.event.CardAssignedEvent;
import com.volvo.emsp.domain.model.enums.CardStatus;
import com.volvo.emsp.domain.service.IdGenerator;
import com.volvo.emsp.domain.service.impl.TestIdGenerator;
import com.volvo.emsp.execption.InvalidBusinessOperationException;
import com.volvo.emsp.testmodel.Emaids;
import com.volvo.emsp.testmodel.Emails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    private static final Long CARD_ID = 1L;
    private static final String RFID_UID = "test-rfid-123";
    private static final String VISIBLE_NUMBER = "CARD-001";
    private static final String EMAIL = "test@example.com";
    private static final String CONTRACT_ID = "CN8VOSXZGADN10";
    private final IdGenerator idGenerator = new TestIdGenerator();

    private Card card;

    @BeforeEach
    void setUp() {
        card = new Card(CARD_ID,RFID_UID, VISIBLE_NUMBER);
    }

    @Test
    void testCardCreation() {
        assertEquals(CARD_ID, card.getCardId());
        assertEquals(RFID_UID, card.getRfidUid());
        assertEquals(VISIBLE_NUMBER, card.getVisibleNumber());
        assertEquals(CardStatus.CREATED, card.getStatus());
        assertNotNull(card.getCreatedAt());
        assertNotNull(card.getLastUpdated());
    }

    @Test
    void testInvalidCardCreation() {
        // empty RFID
        assertThrows(IllegalArgumentException.class, () ->
                new Card(CARD_ID, "", VISIBLE_NUMBER));

        // empty VISIBLE_NUMBER
        assertThrows(IllegalArgumentException.class, () ->
                new Card(CARD_ID, RFID_UID, ""));

        // null cardId
        assertThrows(NullPointerException.class, () ->
                new Card(null, RFID_UID, VISIBLE_NUMBER));

        // null VISIBLE_NUMBER
        assertThrows(IllegalArgumentException.class, () -> newCard(RFID_UID, null));

        // testCreateCardWithWhitespaceRfidUid
        assertThrows(IllegalArgumentException.class, () -> newCard(" ", VISIBLE_NUMBER));

        // testCreateCardWithWhitespaceVisibleNumber
        assertThrows(IllegalArgumentException.class, () -> newCard(RFID_UID, " "));

        // RFID too long
        String longRfid = "a".repeat(256);
        assertThrows(IllegalArgumentException.class, () ->
                new Card(CARD_ID, longRfid, VISIBLE_NUMBER));

        // testCreateCardWithTooLongVisibleNumber
        String longVisibleNumber = "a".repeat(256);
        assertThrows(IllegalArgumentException.class, () ->
                new Card(CARD_ID, RFID_UID, longVisibleNumber));
    }

    @Test
    void testCreateCard() {
        Card card = newCard(RFID_UID, VISIBLE_NUMBER);
        assertEquals(RFID_UID, card.getRfidUid());
        assertEquals(VISIBLE_NUMBER, card.getVisibleNumber());
        assertEquals(CardStatus.CREATED, card.getStatus());
        assertNotNull(card.getCreatedAt());
        assertNotNull(card.getLastUpdated());
        assertNull(card.getAccountId());
        assertNull(card.getContractId());
    }

    @Test
    void testAssignCard() {
        Card card = newCard(RFID_UID, VISIBLE_NUMBER);
        Account account = newAccount(EMAIL, CONTRACT_ID);
        account.activate(); // Account must be activated before assigning card
        card.assignTo(account);
        assertEquals(account.getAccountId(), card.getAccountId());
        assertEquals(account.getContractId(), card.getContractId());
        assertEquals(CardStatus.ASSIGNED, card.getStatus());
        assertTrue(card.getDomainEvents().stream()
                .anyMatch(event -> event instanceof CardAssignedEvent));

    }

    @Test
    void testInvalidAssignToAccount() {

        // null account
        Card card = newCard(RFID_UID, VISIBLE_NUMBER);
        assertThrows(IllegalArgumentException.class, () -> card.assignTo(null));

        // testAssignCardToInactiveAccount
        Account account = newAccount(EMAIL, CONTRACT_ID);
        // Account is in CREATED state (not ACTIVATED)
        assertThrows(InvalidBusinessOperationException.class, () -> card.assignTo(account));

        Account account1 = newAccount(Emails.EMAIL1, Emaids.CONTRACT_ID1);
        Account account2 = newAccount(Emails.EMAIL2, Emaids.CONTRACT_ID2);
        account1.activate();
        account2.activate();
        card.assignTo(account1);
        assertThrows(InvalidBusinessOperationException.class, () -> card.assignTo(account2));
    }


    @Test
    void testActivateCard() {
        Card card = newCard(RFID_UID, VISIBLE_NUMBER);
        Account account = newAccount(EMAIL, CONTRACT_ID);
        account.activate();
        card.assignTo(account);
        card.activate();
        assertEquals(CardStatus.ACTIVATED, card.getStatus());
    }

    @Test
    void testDeactivateCard() {
        Card card = newCard(RFID_UID, VISIBLE_NUMBER);
        Account account = newAccount(EMAIL, CONTRACT_ID);
        account.activate();
        card.assignTo(account);
        card.activate();
        card.deactivate();
        assertEquals(CardStatus.DEACTIVATED, card.getStatus());
    }

    private Card newCard(String rfidUid, String visibleNumber) {
        return new Card(idGenerator.nextId(), rfidUid, visibleNumber);
    }

    @SuppressWarnings("SameParameterValue")
    private Account newAccount(String email, String contractId) {
        return new Account(idGenerator.nextId(), email, contractId);
    }
}