package com.volvo.emsp.application.dto;

import com.volvo.emsp.domain.model.Account;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@SuppressWarnings("unused")
@Schema(description = "Account Data Transfer Object")
public class AccountDTO {

    @Schema(description = "Account ID", example = "12391298439")
    private Long accountId;
    private String email;
    private String emaid;
    private String status;
    private OffsetDateTime lastUpdated;

    public AccountDTO() {
    }

    public AccountDTO(Long id, String email, String emaid, String status, OffsetDateTime lastUpdated) {
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

    public OffsetDateTime getLastUpdated() {
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

    public void setLastUpdated(OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
