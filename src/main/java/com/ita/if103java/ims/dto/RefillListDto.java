package com.ita.if103java.ims.dto;

import org.springframework.stereotype.Component;

@Component
public class RefillListDto {
    private Long id;
    private String name;
    private String itemName;
    private int quantity;

    public RefillListDto() {
    }

    public RefillListDto(Long id, String name, String itemName, int quantity) {
        this.id = id;
        this.name = name;
        this.itemName = itemName;
        this.quantity = quantity;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;

    }

    @Override
    public String toString() {
        return "RefillListDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", itemName='" + itemName + '\'' +
            ", quantity=" + quantity +
            '}';
    }

}
