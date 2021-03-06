package com.ita.if103java.ims.dto;

import java.time.ZonedDateTime;

public class EventDto {
    private Long id;
    private String message;
    private ZonedDateTime date;
    private Long accountId;
    private Long warehouseId;
    private String warehouse;
    private Long authorId;
    private String author;
    private String name;
    private Long transactionId;

    public EventDto() {
    }

    public EventDto(Long id, String message, ZonedDateTime date, Long accountId, Long warehouseId, Long authorId,
                    String name, Long transactionId) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.accountId = accountId;
        this.warehouseId = warehouseId;
        this.authorId = authorId;
        this.name = name;
        this.transactionId = transactionId;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
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
            ", name=" + name +
            ", transactionId=" + transactionId +
            '}';
    }
}
