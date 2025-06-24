package com.volvo.emsp.infrastructure.repository.jpa.integration;

import com.volvo.emsp.domain.model.Account;
import com.volvo.emsp.domain.model.Email;
import com.volvo.emsp.domain.service.IdGenerator;
import com.volvo.emsp.domain.service.impl.TestIdGenerator;
import com.volvo.emsp.infrastructure.repository.jpa.AccountJapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Import({JpaConfig.class, AccountJapRepository.class}) // custom repository need this
@Transactional
public class AccountJpaRepositoryTest {

    @Autowired
    private AccountJapRepository accountJpaRepository;
    private final IdGenerator idGenerator = new TestIdGenerator();


    private static final List<String[]> accountValues = Arrays.asList(
            new String[] {"leoabby@outlook.com", "CN8VOLSXZGQEN0",},
            new String[] {"leoabby2@outlook.com", "CN8VOLSXZGQEN1"},
            new String[] {"leoabby3@outlook.com", "CN8VOLSXZGQEN2"}
    );

    @BeforeEach
    void init () {
        for (String[] accountValue : accountValues) {
            Account account = newAccount(accountValue[0], accountValue[1]);
            accountJpaRepository.save(account);
        }
    }


    @Test
    void testSave() {
        String email = "leoabby4@outlook.com";
        String contractId = "CN8VOLSXZGQEN9";
        Account account = newAccount(email, contractId);
        accountJpaRepository.save(account);
        Optional<Account> optionalAccount = accountJpaRepository.findByEmail(Email.of(email));

        // assert
        assertTrue(optionalAccount.isPresent(), "account should exist");
        Account savedAccount = optionalAccount.get();
        assertEquals(email, savedAccount.getEmail().toString(), "email should same");
        assertNotNull(savedAccount.getAccountId(), "id should exist");
    }

    @Test
    void findByLastUpdatedTime() {
        Page<Account> accounts = accountJpaRepository.findByLastUpdatedBetween(
                LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
                LocalDateTime.of(LocalDate.now(), LocalTime.MAX),
                Pageable.ofSize(10)
        );

        // assert
        assertEquals(accountValues.size(), accounts.getTotalElements(), "query count not right");
    }

    @Test
    void findByNullLastUpdatedTime() {
        Page<Account> accounts = accountJpaRepository.findByLastUpdatedBetween(
                null,
                null,
                Pageable.ofSize(10)
        );

        // assert
        assertEquals(accountValues.size(), accounts.getTotalElements(), "query count not right");
    }

    @Test
    void findByEmail() {
        String email = "leoabby@outlook.com";
        Optional<Account> optionalAccount = accountJpaRepository.findByEmail(Email.of(email));

        // assert
        assertTrue(optionalAccount.isPresent(), "account should exist");
        Account savedAccount = optionalAccount.get();
        assertEquals(email, savedAccount.getEmail().toString(), "email should same");
    }

    @Test
    void findById() {
        Account account = newAccount("leoabby@outlook5.com", "CN8VOLSXZGQEN9");
        Account saved = accountJpaRepository.save(account);
        Optional<Account> optionalAccount = accountJpaRepository.findById(saved.getAccountId());

        // assert
        assertTrue(optionalAccount.isPresent(), "account should exist");
        Account savedAccount = optionalAccount.get();
        assertEquals(saved.getAccountId(), savedAccount.getAccountId(), "id should same");
    }

    @Test
    void existsByEmail() {
        String email = "leoabby@outlook.com";
        boolean exists = accountJpaRepository.existsByEmail(Email.of(email));

        // assert
        assertTrue(exists, "account should exist");
    }

    @SuppressWarnings("SameParameterValue")
    private Account newAccount(String email, String contractId) {
        return new Account(idGenerator.nextId(), email, contractId);
    }
}

