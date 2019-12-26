package com.ita.if103java.ims.entity;

import java.sql.Timestamp;
import java.util.Objects;

public class Transaction {
    private Long id;
    private Timestamp timestamp;
    private Long accountId;
    private Long workerId;
    private Long associateId;
    private Long itemId;
    private Long quantity;
    private Long movedFrom;
    private Long movedTo;
    private TransactionType type;

    public Transaction() {
    }

    public Transaction(Long accountId, Long workerId, Long associateId,
                       Long itemId, Long quantity, Long movedFrom, Long movedTo,
                       TransactionType type) {
        this.accountId = accountId;
        this.workerId = workerId;
        this.associateId = associateId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.movedFrom = movedFrom;
        this.movedTo = movedTo;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Long getAssociateId() {
        return associateId;
    }

    public void setAssociateId(Long associateId) {
        this.associateId = associateId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getMovedFrom() {
        return movedFrom;
    }

    public void setMovedFrom(Long movedFrom) {
        this.movedFrom = movedFrom;
    }

    public Long getMovedTo() {
        return movedTo;
    }

    public void setMovedTo(Long movedTo) {
        this.movedTo = movedTo;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(accountId, that.accountId) &&
            Objects.equals(workerId, that.workerId) &&
            Objects.equals(associateId, that.associateId) &&
            Objects.equals(itemId, that.itemId) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(movedFrom, that.movedFrom) &&
            Objects.equals(movedTo, that.movedTo) &&
            type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, accountId, workerId, associateId, itemId, quantity, movedFrom, movedTo, type);
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + id +
            ", timestamp=" + timestamp +
            ", accountId=" + accountId +
            ", workerId=" + workerId +
            ", associateId=" + associateId +
            ", itemId=" + itemId +
            ", quantity=" + quantity +
            ", movedFrom=" + movedFrom +
            ", movedTo=" + movedTo +
            ", type=" + type +
            '}';
    }
}
