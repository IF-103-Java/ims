package com.ita.if103java.ims.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String unit;
    @NotBlank
    private String description;
    @NotNull
    private int volume;
    @NotNull
    private Long accountId;
    private boolean active;

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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
