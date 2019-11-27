package com.ita.if103java.ims.entity;

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
}
