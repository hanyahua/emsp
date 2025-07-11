package com.volvo.emsp.application.dto;

import com.volvo.emsp.domain.model.Card;
import com.volvo.emsp.domain.model.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@SuppressWarnings("unused")
@Schema(description = "Card Data Transfer Object")
public class CardDTO {

    @Schema(description = "Card ID", example = "12391298439")
    private Long cardId;
    private String rfidUid;
    private String visibleNumber;
    private String contractId;  // 合同ID，可能为空
    @Schema(description = "Card status", example = "ASSIGNED")
    private CardStatus status;

    @Schema(description = "Last updated time", example = "2023-10-01T12:34:56Z")
    private OffsetDateTime lastUpdated;

    @Schema(description = "Created time", example = "2023-10-01T12:34:56Z")
    private OffsetDateTime createdAt;

    @Schema(description = "Account ID", example = "12391298439")
    private Long accountId;     // 关联账户ID，可能为空

    public CardDTO() {}

    // --- getter 和 setter ---

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long id) {
        this.cardId = id;
    }

    public String getRfidUid() {
        return rfidUid;
    }

    public void setRfidUid(String rfidUid) {
        this.rfidUid = rfidUid;
    }

    public String getVisibleNumber() {
        return visibleNumber;
    }

    public void setVisibleNumber(String visibleNumber) {
        this.visibleNumber = visibleNumber;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public CardStatus getStatus() {
        return status;
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public static CardDTO of(Card card) {
        if (card == null) {
            return null;
        }
        CardDTO dto = new CardDTO();
        dto.setCardId(card.getCardId());
        dto.setRfidUid(card.getRfidUid());
        dto.setVisibleNumber(card.getVisibleNumber());
        dto.setContractId(card.getContractId() == null ? null : card.getContractId().toString());
        dto.setStatus(card.getStatus());
        dto.setLastUpdated(card.getLastUpdated());
        dto.setAccountId(card.getAccountId());
        dto.setCreatedAt(card.getCreatedAt());
        return dto;
    }

    @Override
    public String toString() {
        return "CardDTO{" +
                "cardId=" + cardId +
                ", rfidUid='" + rfidUid + '\'' +
                ", visibleNumber='" + visibleNumber + '\'' +
                ", contractId='" + contractId + '\'' +
                ", status=" + status +
                ", lastUpdatedTime=" + lastUpdated +
                ", createdTime=" + createdAt +
                ", accountId=" + accountId +
                '}';
    }
}
