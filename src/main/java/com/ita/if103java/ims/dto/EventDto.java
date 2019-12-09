package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.entity.EventName;
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
    private Long transactionId;

    public EventDto() {
    }

    public static class Builder {
        private EventDto newEventDto = new EventDto();

        public Builder withMessage(String message) {
            newEventDto.message = message;
            return this;
        }

        public Builder withAccountId(long accountId) {
            newEventDto.accountId = accountId;
            return this;
        }

        public Builder withWarehouseId(long warehouseId) {
            newEventDto.warehouseId = warehouseId;
            return this;
        }

        public Builder withAuthorId(long authorId) {
            newEventDto.authorId = authorId;
            return this;
        }

        public Builder withName(EventName name) {
            newEventDto.name = name;
            return this;
        }

        public Builder withDate(ZonedDateTime date) {
            newEventDto.date = date;
            return this;
        }

        public Builder withTransactionId(long transactionId) {
            newEventDto.transactionId = transactionId;
            return this;
        }

        public EventDto build() {
            return newEventDto;
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

    public EventName getName() {
        return name;
    }

    public void setName(EventName name) {
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
