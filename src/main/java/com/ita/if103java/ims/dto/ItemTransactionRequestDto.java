package com.ita.if103java.ims.dto;

import javax.validation.constraints.NotNull;

public class ItemTransactionRequestDto {
    @NotNull
    private ItemDto itemDto;
    @NotNull
    private Long savedItemId;
    @NotNull
    private Long quantity;
    private Long associateId;
    private Long sourceWarehouseId;
    private Long destinationWarehouseId;

    public ItemDto getItemDto() {
        return itemDto;
    }

    public void setItemDto(ItemDto itemDto) {
        this.itemDto = itemDto;
    }

    public Long getSavedItemId() {
        return savedItemId;
    }

    public void setSavedItemId(Long savedItemId) {
        this.savedItemId = savedItemId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getAssociateId() {
        return associateId;
    }

    public void setAssociateId(Long associateId) {
        this.associateId = associateId;
    }

    public Long getSourceWarehouseId() {
        return sourceWarehouseId;
    }

    public void setSourceWarehouseId(Long sourceWarehouseId) {
        this.sourceWarehouseId = sourceWarehouseId;
    }

    public Long getDestinationWarehouseId() {
        return destinationWarehouseId;
    }

    public void setDestinationWarehouseId(Long destinationWarehouseId) {
        this.destinationWarehouseId = destinationWarehouseId;
    }
}
