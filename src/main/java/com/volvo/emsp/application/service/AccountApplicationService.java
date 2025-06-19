package com.volvo.emsp.application.service;

import com.volvo.emsp.application.dto.AccountDTO;
import com.volvo.emsp.domain.model.Account;
import com.volvo.emsp.domain.model.Email;
import com.volvo.emsp.domain.model.enums.AccountStatus;
import com.volvo.emsp.domain.repository.AccountRepository;
import com.volvo.emsp.domain.service.EmaidGenerator;
import com.volvo.emsp.execption.BadRequestException;
import com.volvo.emsp.execption.InvalidBusinessOperationException;
import com.volvo.emsp.execption.ResourceAlreadyExistsException;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountApplicationService {

    private final AccountRepository accountRepository;
    private final EmaidGenerator emaidGenerator;

    public AccountApplicationService(AccountRepository accountRepository, EmaidGenerator emaidGenerator) {
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
        if (!Email.isValid(email)) {
            throw new BadRequestException("Invalid email format." + email);
        }
        // check if email exists
        if (accountRepository.existsByEmail(Email.of(email))) {
            throw new ResourceAlreadyExistsException("Email already exists: " + email);
        }

        Account account = new Account(email, emaidGenerator.generateEmaid());
        // save and get
        account = accountRepository.save(account);

        // update emaid
        return AccountDTO.of(account);
    }

    @Transactional
    public void changeAccountStatus(Long accountId, String targetStatusStr) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        AccountStatus targetStatus;
        try {
            targetStatus = AccountStatus.valueOf(targetStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid account status: " + targetStatusStr);
        }

        switch (targetStatus) {
            case ACTIVATED -> account.activate();
            case DEACTIVATED -> account.deactivate();
            default -> throw new InvalidBusinessOperationException("Can not change account status to : " + targetStatusStr);
        }
        accountRepository.save(account);
    }
}
