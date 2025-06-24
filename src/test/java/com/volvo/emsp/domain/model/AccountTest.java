package com.volvo.emsp.domain.model;

import com.volvo.emsp.domain.model.enums.AccountStatus;
import com.volvo.emsp.domain.service.IdGenerator;
import com.volvo.emsp.domain.service.impl.TestIdGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    
    private static final String email = "leoabby@outlook.com";
    private static final String contractId = "CN8VOLSXZGADN0";
    private final IdGenerator idGenerator = new TestIdGenerator();

    @Test
    public void testCreatedToActivated() {
        Account account = newAccount(email, contractId);
        account.activate();
        assertEquals(AccountStatus.ACTIVATED, account.getStatus());
        assertNotNull(account.getLastUpdated());
    }

    @Test
    public void testCreatedToDeactivated() {
        Account account = newAccount(email, contractId);
        account.deactivate();
        assertEquals(AccountStatus.DEACTIVATED, account.getStatus());
        assertNotNull(account.getLastUpdated());
    }

    @Test
    public void testCreatedToCreated() {
        Account account = newAccount(email, contractId);
        assertEquals(AccountStatus.CREATED, account.getStatus());
        assertNotNull(account.getLastUpdated());
    }

    @Test
    public void testActivatedToDeactivated() {
        Account account = newAccount(email, contractId);
        account.activate();
        account.deactivate();
        assertEquals(AccountStatus.DEACTIVATED, account.getStatus());
        assertNotNull(account.getLastUpdated());
    }


    @Test
    public void testDeactivatedToActivated() {
        Account account = newAccount(email, contractId);
        account.deactivate();
        account.activate();
        assertEquals(AccountStatus.ACTIVATED, account.getStatus());
        assertNotNull(account.getLastUpdated());
    }


    @Test
    public void testLastUpdatedTimeChanged() throws InterruptedException {
        Account account = newAccount(email, contractId);
        LocalDateTime beforeChange = LocalDateTime.now().minusSeconds(1);
        Thread.sleep(20);
        account.activate();
        assertTrue(account.getLastUpdated().isAfter(beforeChange));
    }

    @SuppressWarnings("SameParameterValue")
    private Account newAccount(String email, String contractId) {
        return new Account(idGenerator.nextId(), email, contractId);
    }

}