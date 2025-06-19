package com.volvo.emsp.application.dto;

import com.volvo.emsp.domain.model.Account;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
public class AccountDTO {

    private Long accountId;
    private String email;
    private String emaid;
    private String status;
    private LocalDateTime lastUpdated;

    public AccountDTO(Long id, String email, String emaid, String status, LocalDateTime lastUpdated) {
        this.accountId = id;
        this.email = email;
        this.emaid = emaid;
        this.status = status;
        this.lastUpdated = lastUpdated;
    }

    public static AccountDTO of(Account account) {
        return new AccountDTO(
                account.getAccountId(),
                account.getEmail().toString(),
                account.getContractId() == null ?  null : account.getContractId().toString(),
                account.getStatus().name(),
                account.getLastUpdated()
        );
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getEmaid() {
        return emaid;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmaid(String emaid) {
        this.emaid = emaid;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
