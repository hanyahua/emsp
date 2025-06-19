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
    private LocalDateTime lastUpdatedTime;
    private LocalDateTime createdTime;
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

    public LocalDateTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
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
        dto.setLastUpdatedTime(card.getLastUpdated());
        dto.setAccountId(card.getAccountId());
        dto.setCreatedTime(card.getCreatedAt());
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
                ", lastUpdatedTime=" + lastUpdatedTime +
                ", createdTime=" + createdTime +
                ", accountId=" + accountId +
                '}';
    }
}
