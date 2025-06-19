package com.volvo.emsp.domain.model;

import com.volvo.emsp.domain.model.enums.CardStatus;
import com.volvo.emsp.execption.InvalidBusinessOperationException;
import com.volvo.emsp.testmodel.Emaids;
import com.volvo.emsp.testmodel.Emails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    private static final String RFID_UID = "rfidUid0001";
    private static final String VISIBLE_NUMBER = "0001";
    private static final String EMAIL = "test@example.com";
    private static final String CONTRACT_ID = "CN8VOSXZGADN10";

    @Test
    void testCreateCardWithNullRfidUid() {
        assertThrows(IllegalArgumentException.class, () -> new Card(null, VISIBLE_NUMBER));
    }

    @Test
    void testCreateCardWithNullVisibleNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Card(RFID_UID, null));
    }

    @Test
    void testCreateCardWithEmptyRfidUid() {
        assertThrows(IllegalArgumentException.class, () -> new Card("", VISIBLE_NUMBER));
    }

    @Test
    void testCreateCardWithEmptyVisibleNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Card(RFID_UID, ""));
    }

    @Test
    void testCreateCardWithWhitespaceRfidUid() {
        assertThrows(IllegalArgumentException.class, () -> new Card(" ", VISIBLE_NUMBER));
    }

    @Test
    void testCreateCardWithWhitespaceVisibleNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Card(RFID_UID, " "));
    }

    @Test
    void testCreateCardWithTooLongRfidUid() {
        String tooLongRfidUid = "a".repeat(256);
       assertThrows(IllegalArgumentException.class, () -> new Card(tooLongRfidUid, VISIBLE_NUMBER));
    }

    @Test
    void testCreateCardWithTooLongVisibleNumber() {
        String tooLongVisibleNumber = "a".repeat(256);
        assertThrows(IllegalArgumentException.class, () -> new Card(RFID_UID, tooLongVisibleNumber));
    }

    @Test
    void testCreateCard() {
        Card card = new Card(RFID_UID, VISIBLE_NUMBER);
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
        Card card = new Card(RFID_UID, VISIBLE_NUMBER);
        Account account = new Account(EMAIL, CONTRACT_ID);
        account.activate(); // Account must be activated before assigning card
        card.assignTo(account);
        assertEquals(account.getAccountId(), card.getAccountId());
        assertEquals(account.getContractId(), card.getContractId());
        assertEquals(CardStatus.ASSIGNED, card.getStatus());
    }

    @Test
    void testAssignCardToNullAccount() {
        Card card = new Card(RFID_UID, VISIBLE_NUMBER);
        assertThrows(IllegalArgumentException.class, () -> card.assignTo(null));
    }

    @Test
    void testAssignCardToInactiveAccount() {
        Card card = new Card(RFID_UID, VISIBLE_NUMBER);
        Account account = new Account(EMAIL, CONTRACT_ID);
        // Account is in CREATED state (not ACTIVATED)
        assertThrows(InvalidBusinessOperationException.class, () -> card.assignTo(account));
    }

    @Test
    void testAssignAlreadyAssignedCard() {
        Card card = new Card(RFID_UID, VISIBLE_NUMBER);
        Account account1 = new Account(Emails.EMAIL1, Emaids.CONTRACT_ID1);
        Account account2 = new Account(Emails.EMAIL2, Emaids.CONTRACT_ID2);
        account1.activate();
        account2.activate();
        card.assignTo(account1);
        assertThrows(InvalidBusinessOperationException.class, () -> card.assignTo(account2));
    }

    @Test
    void testActivateCard() {
        Card card = new Card(RFID_UID, VISIBLE_NUMBER);
        Account account = new Account(EMAIL, CONTRACT_ID);
        account.activate();
        card.assignTo(account);

        card.activate();

        assertEquals(CardStatus.ACTIVATED, card.getStatus());
    }

    @Test
    void testDeactivateCard() {
        Card card = new Card(RFID_UID, VISIBLE_NUMBER);
        Account account = new Account(EMAIL, CONTRACT_ID);
        account.activate();
        card.assignTo(account);
        card.activate();

        card.deactivate();

        assertEquals(CardStatus.DEACTIVATED, card.getStatus());
    }
}