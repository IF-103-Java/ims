package com.ita.if103java.ims.entity;

import java.util.Objects;

public class SavedItem {
    private Long id;
    private Long itemId;
    private int quantity;
    private Long warehouseId;

    public SavedItem() {
    }

    public SavedItem(Long itemId, int quantity, Long warehouseId) {
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

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedItem savedItem = (SavedItem) o;
        return quantity == savedItem.quantity &&
            Objects.equals(itemId, savedItem.itemId) &&
            Objects.equals(warehouseId, savedItem.warehouseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, quantity, warehouseId);
    }

    @Override
    public String toString() {
        return "SavedItem{" +
            "id=" + id +
            ", itemId=" + itemId +
            ", quantity=" + quantity +
            ", warehouseId=" + warehouseId +
            '}';
    }
}
