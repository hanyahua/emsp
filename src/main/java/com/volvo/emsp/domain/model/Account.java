package com.volvo.emsp.domain.model;

import com.volvo.emsp.domain.model.enums.AccountStatus;
import com.volvo.emsp.execption.InvalidBusinessOperationException;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(name = "idx_email", columnNames = "email")
}, indexes = {
        @Index(name = "account_idx_last_updated", columnList = "last_updated")
})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false, unique = true)
    @Convert(converter = EmailConverter.class)
    private Email email; // identify an account

    @Column
    @Convert(converter = EmaidConverter.class)
    private Emaid contractId; // EMAID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    protected Account() {
        // JPA
    }

    public Account(String email, String contractId) {
        this.email = new Email(email);
        this.contractId = new Emaid(contractId);
        this.status = AccountStatus.CREATED;
        this.lastUpdated = LocalDateTime.now();
    }

    public Account(Email email, Emaid contractId) {
        this.email = email;
        this.contractId = contractId;
        this.status = AccountStatus.CREATED;
        this.lastUpdated = LocalDateTime.now();
    }

    public void activate() {
        changeStatus(AccountStatus.ACTIVATED);
    }

    public void deactivate() {
        changeStatus(AccountStatus.DEACTIVATED);
    }

    public Long getAccountId() {
        return accountId;
    }

    public Email getEmail() {
        return email;
    }

    public Emaid getContractId() {
        return contractId;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    private void changeStatus(AccountStatus target) {
        if (this.status == target) return; // return
        if (!this.status.canTransitionTo(target)) {
            throw new InvalidBusinessOperationException(
                    "Invalid transition from " + this.status + " to " + target
            );
        }
        this.status = target;
        this.lastUpdated = LocalDateTime.now();
    }
}
