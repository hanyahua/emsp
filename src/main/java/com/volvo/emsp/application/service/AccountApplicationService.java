package com.volvo.emsp.application.service;

import com.volvo.emsp.application.dto.AccountDTO;
import com.volvo.emsp.domain.model.Account;
import com.volvo.emsp.domain.model.Email;
import com.volvo.emsp.domain.model.enums.AccountStatus;
import com.volvo.emsp.domain.repository.AccountRepository;
import com.volvo.emsp.domain.service.EmaidGenerator;
import com.volvo.emsp.domain.service.IdGenerator;
import com.volvo.emsp.execption.BadRequestException;
import com.volvo.emsp.execption.InvalidBusinessOperationException;
import com.volvo.emsp.execption.ResourceAlreadyExistsException;
import com.volvo.emsp.execption.ResourceNotFoundException;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountApplicationService {

    private static final Logger log = LoggerFactory.getLogger(AccountApplicationService.class);
    private final IdGenerator idGenerator;
    private final AccountRepository accountRepository;
    private final EmaidGenerator emaidGenerator;

    public AccountApplicationService(
            IdGenerator idGenerator,
            AccountRepository accountRepository,
            EmaidGenerator emaidGenerator
    ) {
        this.idGenerator = idGenerator;
        this.accountRepository = accountRepository;
        this.emaidGenerator = emaidGenerator;
    }

    @Transactional(readOnly = true)
    public Page<AccountDTO> findAccounts(
            @Nullable LocalDateTime updatedTimeFrom,
            @Nullable LocalDateTime updatedTimeTo,
            Pageable pageable) {
        Page<Account> accounts = accountRepository.findByLastUpdatedBetween(updatedTimeFrom, updatedTimeTo, pageable);
        // map to dto
        return accounts.map(AccountDTO::of);
    }

    @Transactional(readOnly = true)
    public Optional<AccountDTO> findAccountById(Long id) {
        return accountRepository.findById(id).map(AccountDTO::of);
    }

    @Transactional
    public AccountDTO createAccount(String email) {
        log.info("create an account with emil : {}", email);
        if (!Email.isValid(email)) {
            throw new BadRequestException("Invalid email format: " + email);
        }
        // check if email exists
        if (accountRepository.existsByEmail(Email.of(email))) {
            throw new ResourceAlreadyExistsException("Email already exists: " + email);
        }

        Account account = new Account(idGenerator.nextId(), email, emaidGenerator.generateEmaid());
        // save and get
        account = accountRepository.save(account);

        // update emaid
        return AccountDTO.of(account);
    }

    @Transactional
    public void changeAccountStatus(Long accountId, String targetStatusStr) {
        log.info("change account {} status to {}", accountId, targetStatusStr);
        if (targetStatusStr == null || targetStatusStr.isEmpty()) {
            throw new BadRequestException("Invalid account status: " + targetStatusStr);
        }
        AccountStatus targetStatus;
        try {
            targetStatus = AccountStatus.valueOf(targetStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid account status: " + targetStatusStr);
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));

        switch (targetStatus) {
            case ACTIVATED -> account.activate();
            case DEACTIVATED -> account.deactivate();
            default -> throw new InvalidBusinessOperationException("Can not change account status to : " + targetStatusStr);
        }
        accountRepository.save(account);
    }
}
