package com.ita.if103java.ims.entity;

public class Item {
    private Long id;

    private String name;

    private String unit;

    private String description;

    private int volume;

    private Account account;

    private boolean active;

    public Item() {
    }

    public Item(String name, String unit, String description, int volume, Account account, boolean active) {
        this.name = name;
        this.unit = unit;
        this.description = description;
        this.volume = volume;
        this.account = account;
        this.active = active;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
