package com.ita.if103java.ims.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.ita.if103java.ims.entity.TransactionType;

import java.sql.Timestamp;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class TransactionDto {
    private Long id;
    private Timestamp timestamp;
    private TransactionType type;
    private AccountDto account;
    private UserDto worker;
    private ItemDto item;
    private Long quantity;

    @JsonInclude(NON_NULL)
    private AssociateDto associate;
    @JsonInclude(NON_NULL)
    private WarehouseDto movedFrom;
    @JsonInclude(NON_NULL)
    private WarehouseDto movedTo;

    public TransactionDto() {
    }

    public TransactionDto(Long id, Timestamp timestamp, TransactionType type,
                          AccountDto account, UserDto worker, ItemDto item, Long quantity,
                          AssociateDto associate, WarehouseDto movedFrom, WarehouseDto movedTo) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.account = account;
        this.worker = worker;
        this.item = item;
        this.quantity = quantity;
        this.associate = associate;
        this.movedFrom = movedFrom;
        this.movedTo = movedTo;
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

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public AccountDto getAccount() {
        return account;
    }

    public void setAccount(AccountDto account) {
        this.account = account;
    }

    public UserDto getWorker() {
        return worker;
    }

    public void setWorker(UserDto worker) {
        this.worker = worker;
    }

    public ItemDto getItem() {
        return item;
    }

    public void setItem(ItemDto item) {
        this.item = item;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public AssociateDto getAssociate() {
        return associate;
    }

    public void setAssociate(AssociateDto associate) {
        this.associate = associate;
    }

    public WarehouseDto getMovedFrom() {
        return movedFrom;
    }

    public void setMovedFrom(WarehouseDto movedFrom) {
        this.movedFrom = movedFrom;
    }

    public WarehouseDto getMovedTo() {
        return movedTo;
    }

    public void setMovedTo(WarehouseDto movedTo) {
        this.movedTo = movedTo;
    }

    @Override
    public String toString() {
        return "TransactionDto{" +
            "id=" + id +
            ", timestamp=" + timestamp +
            ", type=" + type +
            ", account=" + account +
            ", worker=" + worker +
            ", item=" + item +
            ", quantity=" + quantity +
            ", associate=" + associate +
            ", movedFrom=" + movedFrom +
            ", movedTo=" + movedTo +
            '}';
    }
}
