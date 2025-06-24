package com.volvo.emsp.infrastructure.repository.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "domain_events",  indexes = {
        @Index(name = "idx_status_timestamp", columnList = "status, timestamp")
})
@SuppressWarnings("unused")
public class DomainEventStoreModel {
    @Id
    private String eventId;
    
    @Column(nullable = false)
    private String eventType;
    
    @Column(nullable = false)
    private String aggregateType;  //  "type"
    
    @Column(nullable = false)
    private Long aggregateId;    // ID
    
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON) // for H2
    private String payload;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Instant timestamp;
    
    @Column(nullable = false)
    private String status;
    
    @Version
    private Long version;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public Long getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(Long aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}