package com.ita.if103java.ims.entity;

import java.util.Objects;

public class Address {
    private Long id;
    private String country;
    private String city;
    private String address;
    private String zip;
    private Float latitude;
    private Float longitude;
    private Long warehouseId;
    private Long associateId;

    public Address() {
    }

    public Address(String country, String city, String address, String zip, Float latitude, Float longitude) {
        this.country = country;
        this.city = city;
        this.address = address;
        this.zip = zip;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Address(String country, String city, String address, String zip) {
        this.country = country;
        this.city = city;
        this.address = address;
        this.zip = zip;
    }

    public Address(String country, String city, String address) {
        this.country = country;
        this.city = city;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
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

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getAssociateId() {
        return associateId;
    }

    public void setAssociateId(Long associateId) {
        this.associateId = associateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address1 = (Address) o;
        return Objects.equals(country, address1.country) &&
            Objects.equals(city, address1.city) &&
            Objects.equals(address, address1.address) &&
            Objects.equals(zip, address1.zip) &&
            Objects.equals(latitude, address1.latitude) &&
            Objects.equals(longitude, address1.longitude) &&
            Objects.equals(warehouseId, address1.warehouseId) &&
            Objects.equals(associateId, address1.associateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, city, address, zip, latitude, longitude, warehouseId, associateId);
    }

    @Override
    public String toString() {
        return "Address{" +
            "id=" + id +
            ", country='" + country + '\'' +
            ", city='" + city + '\'' +
            ", address='" + address + '\'' +
            ", zip='" + zip + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", warehouseId=" + warehouseId +
            ", associateId=" + associateId +
            '}';
    }
}
