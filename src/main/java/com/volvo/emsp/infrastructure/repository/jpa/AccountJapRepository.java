package com.volvo.emsp.infrastructure.repository.jpa;

import com.volvo.emsp.domain.model.Account;
import com.volvo.emsp.domain.model.Email;
import com.volvo.emsp.domain.repository.AccountRepository;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public class AccountJapRepository implements AccountRepository {

    private final SpringAccountRepository springAccountRepository;

    public AccountJapRepository(SpringAccountRepository jpaRepository) {
        this.springAccountRepository = jpaRepository;
    }

    @Override
    public Optional<Account> findByEmail(Email email) {
        return springAccountRepository.findByEmail(email);
    }

    @Override
    public Optional<Account> findById(Long accountId) {
        return springAccountRepository.findById(accountId);
    }

    @Override
    public Page<Account> findByLastUpdatedBetween(
            @Nullable LocalDateTime from, @Nullable LocalDateTime to, Pageable pageable) {
        return springAccountRepository.findByLastUpdatedBetween(from, to, pageable);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springAccountRepository.existsByEmail(email);
    }

    @Override
    public Account save(Account account) {
        return springAccountRepository.save(account);
    }
}
