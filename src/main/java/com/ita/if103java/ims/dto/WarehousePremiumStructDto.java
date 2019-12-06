package com.ita.if103java.ims.dto;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WarehousePremiumStructDto {
    private Long id;
    private String name;
    private int level;
    private List<WarehousePremiumStructDto> childs;

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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<WarehousePremiumStructDto> getChilds() {
        return childs;
    }

    public void setChilds(List<WarehousePremiumStructDto> childs) {
        this.childs = childs;
    }
}
