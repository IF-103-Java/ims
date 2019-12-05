package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.entity.EventType;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Component
public class EventDto implements Serializable {
    private Long id;
    private String message;
    private ZonedDateTime date;
    private Long accountId;
    private Long warehouseId;
    private Long authorId;
    private EventType type;
    private Long transactionId;

    public EventDto() {
    }

    public EventDto(String message, ZonedDateTime date, Long accountId, Long authorId, EventType type) {
        this.message = message;
        this.date = date;
        this.accountId = accountId;
        this.authorId = authorId;
        this.type = type;
    }

    public EventDto(String message, ZonedDateTime date, Long accountId, Long authorId, EventType type, Long transactionId) {
        this.message = message;
        this.date = date;
        this.accountId = accountId;
        this.authorId = authorId;
        this.type = type;
        this.transactionId = transactionId;
    }

    public EventDto(String message, ZonedDateTime date, Long accountId, Long warehouseId, Long authorId, EventType type) {
        this.message = message;
        this.date = date;
        this.accountId = accountId;
        this.warehouseId = warehouseId;
        this.authorId = authorId;
        this.type = type;
    }

    public EventDto(Long id, String message, ZonedDateTime date, Long accountId, Long warehouseId, Long authorId, EventType type, Long transactionId) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.accountId = accountId;
        this.warehouseId = warehouseId;
        this.authorId = authorId;
        this.type = type;
        this.transactionId = transactionId;
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
        EventDto eventDto = (EventDto) o;
        return Objects.equals(message, eventDto.message) &&
            Objects.equals(date, eventDto.date) &&
            Objects.equals(accountId, eventDto.accountId) &&
            Objects.equals(warehouseId, eventDto.warehouseId) &&
            Objects.equals(authorId, eventDto.authorId) &&
            type == eventDto.type &&
            Objects.equals(transactionId, eventDto.transactionId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(message, date, accountId, warehouseId, authorId, type, transactionId);
    }

    @Override
    public String toString() {
        return "EventDto{" +
            "id=" + id +
            ", message='" + message + '\'' +
            ", date=" + date +
            ", accountId=" + accountId +
            ", warehouseId=" + warehouseId +
            ", authorId=" + authorId +
            ", type=" + type +
            ", transactionId=" + transactionId +
            '}';
    }
}
