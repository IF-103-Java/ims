package com.ita.if103java.ims.dto;


import com.ita.if103java.ims.entity.TransactionType;

import java.math.BigInteger;
import java.sql.Timestamp;

public class TransactionDto {
    private BigInteger id;
    private Timestamp timestamp;
    private TransactionType type;
    private AccountDto accountDto;
    private UserDto workerId;
    private ItemDto itemDto;
    private Long quantity;
    private AssociateDto associateDto;
    private WarehouseDto movedFrom;
    private WarehouseDto movedTo;

    public TransactionDto() {
    }

    public TransactionDto(BigInteger id, Timestamp timestamp, TransactionType type, AccountDto accountDto,
                          UserDto workerId, ItemDto itemDto, Long quantity) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.accountDto = accountDto;
        this.workerId = workerId;
        this.itemDto = itemDto;
        this.quantity = quantity;
    }

    public TransactionDto(BigInteger id, Timestamp timestamp, TransactionType type, AccountDto accountDto,
                          UserDto workerId, ItemDto itemDto, Long quantity, AssociateDto associateDto) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.accountDto = accountDto;
        this.workerId = workerId;
        this.itemDto = itemDto;
        this.quantity = quantity;
        this.associateDto = associateDto;
    }

    public TransactionDto(BigInteger id, Timestamp timestamp, TransactionType type, AccountDto accountDto,
                          UserDto workerId, ItemDto itemDto, Long quantity, WarehouseDto movedFrom, WarehouseDto movedTo) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.accountDto = accountDto;
        this.workerId = workerId;
        this.itemDto = itemDto;
        this.quantity = quantity;
        this.movedFrom = movedFrom;
        this.movedTo = movedTo;
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

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public AccountDto getAccountDto() {
        return accountDto;
    }

    public void setAccountDto(AccountDto accountDto) {
        this.accountDto = accountDto;
    }

    public UserDto getWorkerId() {
        return workerId;
    }

    public void setWorkerId(UserDto workerId) {
        this.workerId = workerId;
    }

    public ItemDto getItemDto() {
        return itemDto;
    }

    public void setItemDto(ItemDto itemDto) {
        this.itemDto = itemDto;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public AssociateDto getAssociateDto() {
        return associateDto;
    }

    public void setAssociateDto(AssociateDto associateDto) {
        this.associateDto = associateDto;
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
}
