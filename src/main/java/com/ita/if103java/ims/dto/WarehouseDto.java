package com.ita.if103java.ims.dto;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Objects;

@Component
public class WarehouseDto implements Serializable {

    @NotNull(message = "This field must be filled with the auto-generator during creating new warehouse")
    private long id;
    @NotBlank(message = "This field mustn't be empty. Please, enter name of warehouse")
    private String name;
    @NotBlank(message = "This field mustn't be empty. Please, enter some information about warehouse")
    private String info;
    @Null(message = "Please, enter some capacity of warehouse")
    private int capacity;
    @NotNull(message = "This field mustn't be empty. Please, choose option of warehouse")
    private boolean isBottom;
    @NotNull(message = "This field mustn't be empty. Please, choose parent of this warehouse")
    private long parentID;
    @NotNull(message = "This field mustn't be empty. Please, choose account")
    private long accountID;
    @NotNull(message = "This field mustn't be empty. Please, choose ID of top parent")
    private long topWarehouseID;
    @NotNull(message = "This field mustn't be empty. Please, choose option of warehouse")
    private boolean active;

    public WarehouseDto() {
    }

    public WarehouseDto(long id, String name, String info, int capacity, boolean isBottom, long parentID, long accountID,
                        long topWarehouseID, boolean active) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.capacity = capacity;
        this.isBottom = isBottom;
        this.parentID = parentID;
        this.accountID = accountID;
        this.topWarehouseID = topWarehouseID;
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseDto that = (WarehouseDto) o;
        return capacity == that.capacity &&
            isBottom == that.isBottom &&
            parentID == that.parentID &&
            accountID == that.accountID &&
            topWarehouseID == that.topWarehouseID &&
            active == that.active &&
            Objects.equals(name, that.name) &&
            Objects.equals(info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, info, capacity, isBottom, parentID, accountID, topWarehouseID, active);
    }

    @Override
    public String toString() {
        return "WarehouseDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", info='" + info + '\'' +
            ", capacity=" + capacity +
            ", isBottom=" + isBottom +
            ", parentID=" + parentID +
            ", accountID=" + accountID +
            ", topWarehouseID=" + topWarehouseID +
            ", active=" + active +
            '}';
    }
}
