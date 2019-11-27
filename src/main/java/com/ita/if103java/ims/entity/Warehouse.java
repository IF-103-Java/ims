package com.ita.if103java.ims.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Warehouse {
    private long id;
    private String name;
    private String info;
    private int capacity;
    private boolean isBottom;
    private long parentID;
    private long accountID;
    private long topWarehouseID;
    private boolean active;
    private List<Warehouse> children;

    public Warehouse() {
    }

    public Warehouse(long id, String name, String info, int capacity, boolean isBottom,
                     long parentID, long accountID, long topWarehouseID, boolean active) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.capacity = capacity;
        this.isBottom = isBottom;
        this.parentID = parentID;
        this.accountID = accountID;
        this.topWarehouseID = topWarehouseID;
        this.active = active;
        this.children = new ArrayList<>();
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isBottom() {
        return isBottom;
    }

    public void setBottom(boolean bottom) {
        isBottom = bottom;
    }

    public long getParentID() {
        return parentID;
    }

    public void setParentID(long parentID) {
        this.parentID = parentID;
    }

    public long getAccountID() {
        return accountID;
    }

    public void setAccountID(long accountID) {
        this.accountID = accountID;
    }

    public long getTopWarehouseID() {
        return topWarehouseID;
    }

    public void setTopWarehouseID(long topWarehouseID) {
        this.topWarehouseID = topWarehouseID;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Warehouse> getChildren() {
        return children;
    }
    @Override
    public String toString() {
        return "Warehouse{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", info='" + info + '\'' +
            ", capacity=" + capacity +
            ", isBottom=" + isBottom +
            ", parentID=" + parentID +
            ", accountID=" + accountID +
            ", active=" + active +
            ", children=" + Arrays.toString(children.toArray()) +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warehouse warehouse = (Warehouse) o;
        return id == warehouse.id &&
            accountID == warehouse.accountID &&
            Objects.equals(name, warehouse.name) &&
            Objects.equals(info, warehouse.info) &&
            parentID == warehouse.parentID &&
            capacity == warehouse.capacity &&
            isBottom == warehouse.isBottom &&
            active == warehouse.active &&
            Objects.equals(children, warehouse.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, info, capacity, isBottom, parentID, accountID, active, children);
    }
}



