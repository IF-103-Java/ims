package com.ita.if103java.ims.dto;

import org.springframework.stereotype.Component;

@Component
public class PopularItemsDto {
    private String name;
    private Long quantity;

    public PopularItemsDto() {
    }

    public PopularItemsDto(String name, Long quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "PopularItemsDto{" +
            "name='" + name + '\'' +
            ", quantity=" + quantity +
            '}';
    }
}
