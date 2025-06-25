package com.volvo.emsp.domain.repository;

import com.volvo.emsp.domain.model.Account;
import com.volvo.emsp.domain.model.Email;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface AccountRepository {

    Optional<Account> findByEmail(Email email);

    Optional<Account> findById(Long accountId);

    Page<Account> findByLastUpdatedBetween(@Nullable OffsetDateTime from, @Nullable OffsetDateTime to, Pageable pageable);

    boolean existsByEmail(Email email);

    Account save(Account account);
}
