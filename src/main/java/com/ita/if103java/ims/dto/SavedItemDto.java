package com.ita.if103java.ims.dto;

import javax.validation.constraints.NotNull;

public class SavedItemDto {
    private Long id;
    @NotNull
    private Long itemId;
    @NotNull
    private int quantity;
    @NotNull
    private Long warehouseId;

    public SavedItemDto() {
    }

    public SavedItemDto(Long id, Long itemId, int quantity, Long warehouseId) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
        this.warehouseId = warehouseId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }
}
