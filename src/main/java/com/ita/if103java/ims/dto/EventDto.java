package com.ita.if103java.ims.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ita.if103java.ims.dto.transfer.ExistData;
import com.ita.if103java.ims.dto.transfer.NewData;
import com.ita.if103java.ims.entity.EventType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.ZonedDateTime;
import java.util.Objects;

public class EventDto {
    @Null(groups = {NewData.class},
        message = "This field must be empty due to auto generation")
    @NotNull(groups = {ExistData.class},
        message = "This field can't be empty")
    private Long id;
    @NotBlank(groups = {NewData.class, ExistData.class},
        message = "Provide a message")
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private ZonedDateTime date;
    @NotNull(groups = {NewData.class, ExistData.class},
        message = "This field can't be empty")
    private Long accountId;
    private Long warehouseId;
    @NotNull(groups = {NewData.class, ExistData.class},
        message = "This field can't be empty")
    private Long authorId;
    @NotNull(groups = {NewData.class, ExistData.class},
        message = "This field can't be empty")
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

    public EventDto(Long id,  String message, ZonedDateTime date, Long accountId, Long warehouseId,  Long authorId, EventType type, Long transactionId) {
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
