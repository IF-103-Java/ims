package com.ita.if103java.ims.entity;

public class TopWarehouseAddress {
    private Long warehouseId;
    private String warehouseName;
    private String country;
    private String city;
    private String address;
    private Float latitude;
    private Float longitude;

    public TopWarehouseAddress() {
    }

    public TopWarehouseAddress(Long warehouseId, String warehouseName, String country, String city,
                               String address, Float latitude, Float longitude) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.country = country;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "TopWarehouseAddress{" +
            "warehouseId=" + warehouseId +
            ", warehouseName='" + warehouseName + '\'' +
            ", country='" + country + '\'' +
            ", city='" + city + '\'' +
            ", address='" + address + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
    }
}
