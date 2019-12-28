package com.ita.if103java.ims.dto;

public class WarehouseAddressDto extends AddressDto {
    private Long warehouseId;

    public WarehouseAddressDto() {
    }

    public WarehouseAddressDto(Long id, String country, String city, String address,
                               String zip, Float latitude, Float longitude, Long warehouseId) {
        super(id, country, city, address, zip, latitude, longitude);
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    @Override
    public String toString() {
        return "WarehouseAddressDto{" +
            "warehouseId=" + warehouseId +
            ", id=" + getId() +
            ", country='" + getCountry() + '\'' +
            ", city='" + getCity() + '\'' +
            ", address='" + getAddress() + '\'' +
            ", zip='" + getZip() + '\'' +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            '}';
    }
}
