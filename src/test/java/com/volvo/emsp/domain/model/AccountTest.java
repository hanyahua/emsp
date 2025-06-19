package com.volvo.emsp.domain.model;

import com.volvo.emsp.domain.model.enums.AccountStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    
    private static final String email = "leoabby@outlook.com";
    private static final String contractId = "CN8VOLSXZGADN0";

    @Test
    public void testCreatedToActivated() {
        Account account = new Account(email, contractId);
        account.activate();
        assertEquals(AccountStatus.ACTIVATED, account.getStatus());
        assertNotNull(account.getLastUpdated());
    }

    @Test
    public void testCreatedToDeactivated() {
        Account account = new Account(email, contractId);
        account.deactivate();
        assertEquals(AccountStatus.DEACTIVATED, account.getStatus());
        assertNotNull(account.getLastUpdated());
    }

    @Test
    public void testCreatedToCreated() {
        Account account = new Account(email, contractId);
        assertEquals(AccountStatus.CREATED, account.getStatus());
        assertNotNull(account.getLastUpdated());
    }

    @Test
    public void testActivatedToDeactivated() {
        Account account = new Account(email, contractId);
        account.activate();
        account.deactivate();
        assertEquals(AccountStatus.DEACTIVATED, account.getStatus());
        assertNotNull(account.getLastUpdated());
    }


    @Test
    public void testDeactivatedToActivated() {
        Account account = new Account(email, contractId);
        account.deactivate();
        account.activate();
        assertEquals(AccountStatus.ACTIVATED, account.getStatus());
        assertNotNull(account.getLastUpdated());
    }


    @Test
    public void testLastUpdatedTimeChanged() throws InterruptedException {
        Account account = new Account(email, contractId);
        LocalDateTime beforeChange = LocalDateTime.now().minusSeconds(1);
        Thread.sleep(20);
        account.activate();
        assertTrue(account.getLastUpdated().isAfter(beforeChange));
    }

}