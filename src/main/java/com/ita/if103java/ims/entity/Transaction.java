package com.ita.if103java.ims.entity;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Objects;

public class Transaction {
    private BigInteger id;
    private Timestamp timestamp;
    private Long accountId;
    private Long associateId;
    private Long itemId;
    private Long quantity;
    private Long movedFrom;
    private Long movedTo;
    private Transaction.Type type;

    public Transaction() {
    }

    public Transaction(Long accountId, Long associateId,
                       Long itemId, Long quantity, Long movedFrom, Long movedTo, Type type) {
        this.accountId = accountId;
        this.associateId = associateId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.movedFrom = movedFrom;
        this.movedTo = movedTo;
        this.type = type;
    }

    public Transaction(Long accountId, Long itemId, Long quantity, Long movedFrom, Long movedTo) {
        this.accountId = accountId;
        this.associateId = null;
        this.itemId = itemId;
        this.quantity = quantity;
        this.movedFrom = movedFrom;
        this.movedTo = movedTo;
        this.type = Type.MOVE;
    }

    public Transaction(Long accountId, Long associateId, Long itemId, Long quantity, Type type) {
        this.accountId = accountId;
        this.associateId = associateId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.movedFrom = null;
        this.movedTo = null;
        assert type != Type.MOVE;
        this.type = type;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(accountId, that.accountId) &&
            Objects.equals(associateId, that.associateId) &&
            Objects.equals(itemId, that.itemId) &&
            Objects.equals(quantity, that.quantity) &&
            Objects.equals(movedFrom, that.movedFrom) &&
            Objects.equals(movedTo, that.movedTo) &&
            type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, accountId, associateId, itemId, quantity, movedFrom, movedTo, type);
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + id +
            ", timestamp=" + timestamp +
            ", accountId=" + accountId +
            ", associateId=" + associateId +
            ", itemId=" + itemId +
            ", quantity=" + quantity +
            ", movedFrom=" + movedFrom +
            ", movedTo=" + movedTo +
            ", type=" + type +
            '}';
    }

    public enum Type {
        IN, OUT, MOVE
    }
}
