package com.volvo.emsp.infrastructure.jpa.repository;


import com.volvo.emsp.domain.model.Account;
import com.volvo.emsp.domain.model.Email;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SpringAccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(Email email);

    @Query("""
        SELECT a FROM Account a
        WHERE (:from IS NULL OR a.lastUpdated >= :from)
          AND (:to IS NULL OR a.lastUpdated <= :to)
    """)
    Page<Account> findByLastUpdatedBetween(@Nullable LocalDateTime from, @Nullable LocalDateTime to, Pageable pageable);

    boolean existsByEmail(Email email);
}
