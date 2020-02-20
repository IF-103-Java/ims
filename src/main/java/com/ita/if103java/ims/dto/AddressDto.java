package com.ita.if103java.ims.dto;

import java.util.Objects;

public class AddressDto {
    private Long id;
    private String country;
    private String city;
    private String address;
    private String zip;
    private Float latitude;
    private Float longitude;

    public AddressDto() {
    }

    public AddressDto(Long id, String country, String city, String address, String zip, Float latitude, Float longitude) {
        this.id = id;
        this.country = country;
        this.city = city;
        this.address = address;
        this.zip = zip;
        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressDto that = (AddressDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(country, that.country) &&
            Objects.equals(city, that.city) &&
            Objects.equals(address, that.address) &&
            Objects.equals(zip, that.zip) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, country, city, address, zip, latitude, longitude);
    }

    @Override
    public String toString() {
        return "AddressDto{" +
            "id=" + id +
            ", country='" + country + '\'' +
            ", city='" + city + '\'' +
            ", address='" + address + '\'' +
            ", zip='" + zip + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
    }
}
