package com.ita.if103java.ims.dto;

import java.util.List;

public class WarehousePremiumStructDto {
    private Long id;
    private String name;
    private Long charge = 0L;
    private Long capacity = 0L;
    private List<WarehousePremiumStructDto> childs;

    public WarehousePremiumStructDto() {
    }

    public WarehousePremiumStructDto(Long id, String name, Long charge, Long capacity, List<WarehousePremiumStructDto> childs) {
        this.id = id;
        this.name = name;
        this.charge = charge;
        this.capacity = capacity;
        this.childs = childs;
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

    public Long getCharge() {
        return charge;
    }

    public void setCharge(Long charge) {
        this.charge = charge;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public List<WarehousePremiumStructDto> getChilds() {
        return childs;
    }

    public void setChilds(List<WarehousePremiumStructDto> childs) {
        this.childs = childs;
    }
}
