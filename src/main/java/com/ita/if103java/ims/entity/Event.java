package com.ita.if103java.ims.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotNull
    @Column(name = "message", updatable = false, nullable = false)
    private String message;

    @Column(name = "date", updatable = false, nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private ZonedDateTime date;

    @NotNull
    @Column(name = "account_id", updatable = false, nullable = false)
    private Long accountId;

    @Column(name = "warehouse_id", updatable = false)
    private Long warehouseId;

    @NotNull
    @Column(name = "author_id", updatable = false, nullable = false)
    private Long authorId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", updatable = false, nullable = false)
    private EventType type;

    @Column(name = "transaction_id", updatable = false)
    private Long transactionID;

    public Event() {
    }

    public Event(String message, Long accountId,
                 Long warehouseId, Long authorId, EventType type, Long transactionID) {
        this.message = message;
        this.date = ZonedDateTime.now();
        this.accountId = accountId;
        this.warehouseId = warehouseId;
        this.authorId = authorId;
        this.type = type;
        this.transactionID = transactionID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Long transactionID) {
        this.transactionID = transactionID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return accountId == event.accountId &&
                warehouseId == event.warehouseId &&
                authorId == event.authorId &&
                transactionID == event.transactionID &&
                Objects.equals(message, event.message) &&
                Objects.equals(date, event.date) &&
                type == event.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, date, accountId, warehouseId, authorId, type, transactionID);
    }
}
