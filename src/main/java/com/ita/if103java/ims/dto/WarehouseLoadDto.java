package com.ita.if103java.ims.dto;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class WarehouseLoadDto {
    private Long id;
    private Long capacity;
    private Long charge;

    public WarehouseLoadDto() {
    }

    public WarehouseLoadDto(Long id, Long capacity, Long charge) {
        this.id = id;
        this.capacity = capacity;
        this.charge = charge;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Long getCharge() {
        return charge;
    }

    public void setCharge(Long charge) {
        this.charge = charge;
    }

    @Override
    public String toString() {
        return "WarehouseLoadDto{" +
            "id=" + id +
            ", capacity=" + capacity +
            ", charge=" + charge +
            '}';
    }
}
