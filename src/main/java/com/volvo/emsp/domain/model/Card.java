package com.volvo.emsp.domain.model;

import com.volvo.emsp.domain.model.enums.AccountStatus;
import com.volvo.emsp.domain.model.enums.CardStatus;

import com.volvo.emsp.execption.InvalidBusinessOperationException;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Card entity representing the charging card linked to an account.
 * Each card has a unique identifier (cardId) and can be assigned to an account.
 */
@Entity
@Table(name = "cards", indexes = {
        @Index(name = "idx_last_updated", columnList = "last_updated"),
        @Index(name = "idx_rfid_uid", columnList = "rfid_uid")
})
public class Card {

    /**
     * Unique identifier for the card.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "contract_id")
    @Convert(converter = EmaidConverter.class)
    private Emaid contractId;

    /**
     * The unique RFID identifier associated with the card.
     */
    @Column(name = "rfid_uid", nullable = false, unique = true, length = 100, updatable = false)
    private String rfidUid;

    /**
     * The visible card number that can be shown on the card.
     */
    @Column(name = "visible_number", nullable = false, unique = true, length = 100, updatable = false)
    private String visibleNumber;

    /**
     * The status of the card (e.g., ACTIVE, DEACTIVATED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;

    /**
     * The account to which this card is assigned.
     */
    @JoinColumn(name = "account_id", nullable = false)
    private Long accountId;

    /**
     * The date and time when the card was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The date and time when the card's status was last updated.
     */
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    /**
     * Default constructor.
     */
    protected Card() {
    }

    /**
     * Constructor to initialize Card with RFID UID and Visible Number.
     */
    public Card(String rfidUid, String visibleNumber) {
        if (StringUtils.isBlank(rfidUid)) {
            throw new IllegalArgumentException("RFID UID must not be blank.");
        }
        if (StringUtils.isBlank(visibleNumber)) {
            throw new IllegalArgumentException("Visible number must not be blank.");
        }
        if (rfidUid.length() > 100) {
            throw new IllegalArgumentException("RFID UID must be less than or equal to 100 characters.");
        }
        if (visibleNumber.length() > 100) {
            throw new IllegalArgumentException("RFID UID must be less than or equal to 255 characters.");
        }
        this.rfidUid = rfidUid;
        this.visibleNumber = visibleNumber;
        this.status = CardStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public void activate() {
        changeStatus(CardStatus.ACTIVATED);
    }

    public void deactivate() {
        changeStatus(CardStatus.DEACTIVATED);
    }

    public void assignTo(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null.");
        }

        if (this.status != CardStatus.CREATED) {
            throw new InvalidBusinessOperationException("Only cards in CREATED state can be assigned.");
        }

        if (this.accountId != null) {
            throw new InvalidBusinessOperationException("Card is already assigned to an account.");
        }

        if (account.getStatus() != AccountStatus.ACTIVATED) {
            throw new InvalidBusinessOperationException("Account must be activated to assign a card.");
        }

        this.accountId = account.getAccountId();
        this.contractId = account.getContractId();
        this.status = CardStatus.ASSIGNED;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getCardId() {
        return cardId;
    }

    public Emaid getContractId() {
        return contractId;
    }

    public String getRfidUid() {
        return rfidUid;
    }

    public String getVisibleNumber() {
        return visibleNumber;
    }

    public CardStatus getStatus() {
        return status;
    }

    public Long getAccountId() {
        return accountId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    // Update card status and lastUpdated field
    private void changeStatus(CardStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Target status must not be null.");
        }

        if (!this.status.canTransitionTo(newStatus)) {
            throw new InvalidBusinessOperationException(
                    String.format("Invalid status transition: %s → %s", this.status, newStatus)
            );
        }

        this.status = newStatus;
        this.lastUpdated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", contractId='" + contractId + '\'' +
                ", rfidUid='" + rfidUid + '\'' +
                ", visibleNumber='" + visibleNumber + '\'' +
                ", status=" + status +
                ", accountId=" + accountId +
                ", createdTime=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
