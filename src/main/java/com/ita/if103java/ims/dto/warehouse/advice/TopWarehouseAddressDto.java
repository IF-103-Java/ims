package com.ita.if103java.ims.dto.warehouse.advice;

public class TopWarehouseAddressDto {
    private Long warehouseId;
    private String warehouseName;
    private Address address;

    public TopWarehouseAddressDto() {
    }

    public TopWarehouseAddressDto(Long warehouseId, String warehouseName, Address address) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.address = address;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "TopWarehouseAddressDto{" +
            "warehouseId=" + warehouseId +
            ", warehouseName='" + warehouseName + '\'' +
            ", address=" + address +
            '}';
    }
}
