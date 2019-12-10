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
    private EventName name;
    private Long transactionId;

    public Event() {
    }

    public static class Builder {
        private Event newEvent = new Event();

        public Builder withMessage(String message) {
            newEvent.message = message;
            return this;
        }

        public Builder withAccountId(long accountId) {
            newEvent.accountId = accountId;
            return this;
        }

        public Builder withWarehouseId(long warehouseId) {
            newEvent.warehouseId = warehouseId;
            return this;
        }

        public Builder withAuthorId(long authorId) {
            newEvent.authorId = authorId;
            return this;
        }

        public Builder withName(EventName name) {
            newEvent.name = name;
            return this;
        }

        public Builder withDate(ZonedDateTime date) {
            newEvent.date = date;
            return this;
        }

        public Builder withTransactionId(long transactionId) {
            newEvent.transactionId = transactionId;
            return this;
        }

        public Event build() {
            return newEvent;
        }
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

    public void setName(EventName name) {
        this.name = name;
    }

    public EventName getName() {
        return name;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(message, event.message) &&
            Objects.equals(date, event.date) &&
            Objects.equals(accountId, event.accountId) &&
            Objects.equals(warehouseId, event.warehouseId) &&
            Objects.equals(authorId, event.authorId) &&
            name == event.name &&
            Objects.equals(transactionId, event.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, date, accountId, warehouseId, authorId, name, transactionId);
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + id +
            ", message='" + message + '\'' +
            ", date=" + date +
            ", accountId=" + accountId +
            ", warehouseId=" + warehouseId +
            ", authorId=" + authorId +
            ", name=" + name +
            ", transactionId=" + transactionId +
            '}';
    }
}
