package com.ita.if103java.ims.dto.warehouse.advice;

import java.util.Objects;

public class TopWarehouseAddressDto {
    private Long id;
    private String name;
    private Address address;

    public TopWarehouseAddressDto() {
    }

    public TopWarehouseAddressDto(Long id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "TopWarehouseAddressDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", address=" + address +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopWarehouseAddressDto that = (TopWarehouseAddressDto) o;
        return id.equals(that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address);
    }
}
