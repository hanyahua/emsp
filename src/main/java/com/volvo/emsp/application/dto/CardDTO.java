package com.volvo.emsp.application.dto;

import com.volvo.emsp.domain.model.Card;
import com.volvo.emsp.domain.model.enums.CardStatus;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
public class CardDTO {

    private Long cardId;
    private String rfidUid;
    private String visibleNumber;
    private String contractId;  // 合同ID，可能为空
    private CardStatus status;
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;
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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
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
