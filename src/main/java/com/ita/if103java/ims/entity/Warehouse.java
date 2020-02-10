package com.ita.if103java.ims.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Warehouse {
    private Long id;
    private String name;
    private String info;
    private Integer capacity;
    private boolean isBottom;
    private Long parentID;
    private Long accountID;
    private Long topWarehouseID;
    private boolean active;
    private List<Warehouse> children;
    private List<String> path = new ArrayList<>();

    public Warehouse() {
    }

    public Warehouse(Long id, String name, String info, Integer capacity, boolean isBottom,
                     Long parentID, Long accountID, Long topWarehouseID, boolean active) {
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

    public boolean isTopLevel() {
        return this.getParentID() == null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public boolean isBottom() {
        return isBottom;
    }

    public void setBottom(boolean bottom) {
        isBottom = bottom;
    }

    public Long getParentID() {
        return parentID;
    }

    public void setParentID(Long parentID) {
        this.parentID = parentID;
    }

    public Long getAccountID() {
        return accountID;
    }

    public void setAccountID(Long accountID) {
        this.accountID = accountID;
    }

    public Long getTopWarehouseID() {
        return topWarehouseID;
    }

    public void setTopWarehouseID(Long topWarehouseID) {
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

    public void setChildren() {
        this.children = children;
    }

//    public void addChild(Warehouse warehouse){children.add(warehouse);}

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
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
        return Objects.equals(name, warehouse.name) &&
            Objects.equals(info, warehouse.info) &&
            topWarehouseID == warehouse.topWarehouseID &&
            parentID == warehouse.parentID &&
            accountID == warehouse.accountID &&
            capacity == warehouse.capacity &&
            isBottom == warehouse.isBottom &&
            active == warehouse.active &&
            Objects.equals(children, warehouse.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, info, capacity, isBottom, parentID, accountID, topWarehouseID, active, children);
    }
}



