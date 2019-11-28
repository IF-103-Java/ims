package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.entity.Item;
import com.ita.if103java.ims.entity.Warehouse;

import javax.validation.constraints.NotNull;

public class SavedItemDto {
    @NotNull
    private Long itemId;
    @NotNull
    private int quantity;
    @NotNull
    private Long idWarehouse;

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

    public Long getIdWarehouse() {
        return idWarehouse;
    }

    public void setIdWarehouse(Long idWarehouse) {
        this.idWarehouse = idWarehouse;
    }
}
