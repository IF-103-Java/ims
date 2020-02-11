package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.dto.transfer.ExistData;
import com.ita.if103java.ims.dto.transfer.NewData;
import com.ita.if103java.ims.entity.Warehouse;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WarehouseDto implements Serializable {
    @Null(groups = {NewData.class},
        message = "This field must be filled with the auto-generator during warehouse creation")
    @NotNull(groups = {ExistData.class},
        message = "This field mustn't be empty")
    private Long id;

    @NotBlank(groups = {NewData.class, ExistData.class},
        message = "This field mustn't be empty")
    private String name;

    @NotBlank(groups = {NewData.class, ExistData.class})
    private String info;

    @Null(groups = {NewData.class, ExistData.class},
        message = "This field should be filled if that warehouse is on bottom level")
    private Integer capacity;

    private boolean isBottom;

    @Null(groups = {NewData.class, ExistData.class},
        message = "This field should be filled if that warehouse is on top level")
    private Long parentID;

    @NotNull(groups = {NewData.class, ExistData.class},
        message = "This field mustn't be empty")
    private Long accountID;

    private Long topWarehouseID;

    private boolean active;

    private AddressDto addressDto;

    private List<String> path;

    public WarehouseDto() {
    }

    public WarehouseDto(Long id, String name, String info, Integer capacity, boolean isBottom, Long parentID, Long accountID,
                        Long topWarehouseID, boolean active, AddressDto addressDto) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.capacity = capacity;
        this.isBottom = isBottom;
        this.parentID = parentID;
        this.accountID = accountID;
        this.topWarehouseID = topWarehouseID;
        this.active = active;
        this.addressDto = addressDto;
        this.path = new ArrayList<>();
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

    public AddressDto getAddressDto() {
        return addressDto;
    }

    public void setAddressDto(AddressDto addressDto) {
        this.addressDto = addressDto;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseDto that = (WarehouseDto) o;
        return capacity.equals(that.capacity) &&
            isBottom == that.isBottom &&
            parentID.equals(that.parentID) &&
            accountID.equals(that.accountID) &&
            topWarehouseID.equals(that.topWarehouseID) &&
            active == that.active &&
            Objects.equals(name, that.name) &&
            Objects.equals(info, that.info) &&
            Objects.equals(path, that.path) &&
            Objects.equals(addressDto, that.addressDto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, info, capacity, isBottom, parentID, accountID, topWarehouseID, active, addressDto, path);
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
            ", addressDto=" + addressDto +
            ", path=" + path +
            '}';
    }
}
