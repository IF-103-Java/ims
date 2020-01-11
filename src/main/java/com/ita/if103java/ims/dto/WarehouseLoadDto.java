package com.ita.if103java.ims.dto;

public class WarehouseLoadDto {
    private Long id;
    private String name;
    private Long capacity;
    private Long charge;

    public WarehouseLoadDto() {
    }

    public WarehouseLoadDto(Long id, String name, Long capacity, Long charge) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.charge = charge;
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
}
