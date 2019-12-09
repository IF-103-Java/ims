package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.entity.EventName;
import com.ita.if103java.ims.entity.EventType;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Component
public class EventDto implements Serializable {
    private Long id;
    private String message;
    private ZonedDateTime date;
    private Long accountId;
    private Long warehouseId;
    private Long authorId;
    private EventName name;
    private EventType type;
    private Long transactionId;

    public EventDto() {
    }

    public EventDto(String message, ZonedDateTime date, Long accountId, Long authorId, EventName name) {
        this.message = message;
        this.date = date;
        this.accountId = accountId;
        this.authorId = authorId;
        this.name = name;
        this.type = name.getType();
    }

    public EventDto(String message, ZonedDateTime date, Long accountId, Long warehouseId, Long authorId, EventName name) {
        this.message = message;
        this.date = date;
        this.accountId = accountId;
        this.warehouseId = warehouseId;
        this.authorId = authorId;
        this.name = name;
        this.type = name.getType();
    }

    public EventDto(String message, ZonedDateTime date, Long accountId, Long authorId, EventName name, Long transactionId) {
        this.message = message;
        this.date = date;
        this.accountId = accountId;
        this.authorId = authorId;
        this.name = name;
        this.type = name.getType();
        this.transactionId = transactionId;
    }

    public EventDto(String message, ZonedDateTime date, Long accountId, Long warehouseId, Long authorId, EventName name, Long transactionId) {
        this.message = message;
        this.date = date;
        this.accountId = accountId;
        this.warehouseId = warehouseId;
        this.authorId = authorId;
        this.name = name;
        this.type = name.getType();
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

    public EventName getName() {
        return name;
    }

    public void setName(EventName name) {
        this.name = name;
        this.type = name.getType();
    }

    public EventType getType() {
        return type;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
