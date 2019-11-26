package com.ita.if103java.ims.entity;

import java.util.Objects;

public class AccountType {
    private Long id;
    private Type name;
    private Integer maxWarhouses;
    private Integer maxWarhouseDepth;
    private Integer maxUsers;
    private Integer maxSuppliers;
    private Integer maxClients;
    private boolean active;

    public AccountType() {

    }

    public AccountType(Long id, Type name, Integer maxWarhouses, Integer maxWarhouseDepth, Integer maxUsers, Integer maxSuppliers, Integer maxClients, boolean active) {
        this.id = id;
        this.name = name;
        this.maxWarhouses = maxWarhouses;
        this.maxWarhouseDepth = maxWarhouseDepth;
        this.maxUsers = maxUsers;
        this.maxSuppliers = maxSuppliers;
        this.maxClients = maxClients;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getName() {
        return name;
    }

    public void setName(Type name) {
        this.name = name;
    }

    public Integer getMaxWarhouses() {
        return maxWarhouses;
    }

    public void setMaxWarhouses(Integer maxWarhouses) {
        this.maxWarhouses = maxWarhouses;
    }

    public Integer getMaxWarhouseDepth() {
        return maxWarhouseDepth;
    }

    public void setMaxWarhouseDepth(Integer maxWarhouseDepth) {
        this.maxWarhouseDepth = maxWarhouseDepth;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Integer getMaxSuppliers() {
        return maxSuppliers;
    }

    public void setMaxSuppliers(Integer maxSuppliers) {
        this.maxSuppliers = maxSuppliers;
    }

    public Integer getMaxClients() {
        return maxClients;
    }

    public void setMaxClients(Integer maxClients) {
        this.maxClients = maxClients;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "AccountType{" +
            "id=" + id +
            ", name=" + name +
            ", maxWarhouses=" + maxWarhouses +
            ", maxWarhouseDepth=" + maxWarhouseDepth +
            ", maxUsers=" + maxUsers +
            ", maxSuppliers=" + maxSuppliers +
            ", maxClients=" + maxClients +
            ", active=" + active +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountType)) return false;
        AccountType that = (AccountType) o;
        return isActive() == that.isActive() &&
            getId().equals(that.getId()) &&
            getName() == that.getName() &&
            getMaxWarhouses().equals(that.getMaxWarhouses()) &&
            getMaxWarhouseDepth().equals(that.getMaxWarhouseDepth()) &&
            getMaxUsers().equals(that.getMaxUsers()) &&
            getMaxSuppliers().equals(that.getMaxSuppliers()) &&
            getMaxClients().equals(that.getMaxClients());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getMaxWarhouses(), getMaxWarhouseDepth(), getMaxUsers(), getMaxSuppliers(), getMaxClients(), isActive());
    }
}
