package com.ita.if103java.ims.entity;

import java.util.Objects;

public class SavedItem {
    private Long id;
    private Item item;
    private int quantity;
    private Warehouse warehouse;

    public SavedItem() {
    }

    public SavedItem(Item item, int quantity, Warehouse warehouse) {
        this.item = item;
        this.quantity = quantity;
        this.warehouse = warehouse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedItem savedItem = (SavedItem) o;
        return quantity == savedItem.quantity &&
            Objects.equals(item, savedItem.item) &&
            Objects.equals(warehouse, savedItem.warehouse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, quantity, warehouse);
    }

    @Override
    public String toString() {
        return "SavedItem{" +
            "id=" + id +
            ", item=" + item +
            ", quantity=" + quantity +
            ", warehouse=" + warehouse +
            '}';
    }
}
