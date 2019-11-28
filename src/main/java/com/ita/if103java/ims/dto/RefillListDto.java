package com.ita.if103java.ims.dto;

import javax.validation.constraints.NotNull;

public class RefillListDto {
    @NotNull
    private String name;
    @NotNull
    private String item_name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public RefillListDto(@NotNull String name, @NotNull String item_name) {
        this.name = name;
        this.item_name = item_name;
    }

    public RefillListDto() {
    }

    @Override
    public String toString() {
        return "RefillListDto{" +
            "name=" + name +
            ", item_name='" + item_name + '\'' +
            '}';
    }
}
