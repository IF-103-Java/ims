package com.ita.if103java.ims.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;
import java.util.Objects;

public class Event {
    private Long id;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private ZonedDateTime date;
    private Long accountId;
    private Long warehouseId;
    private Long authorId;
    private EventType type;
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
