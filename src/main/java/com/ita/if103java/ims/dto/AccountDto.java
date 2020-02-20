package com.ita.if103java.ims.dto;

import java.util.Objects;

public class AccountDto {
    private Long id;
    private String name;
    private Long typeId;
    private boolean active;

    public AccountDto() {

    }

    public AccountDto(Long id, String name, Long typeId, boolean active) {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
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

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountDto that = (AccountDto) o;
        return active == that.active &&
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(typeId, that.typeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, typeId, active);
    }

    @Override
    public String toString() {
        return "AccountDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", typeId=" + typeId +
            ", active=" + active +
            '}';
    }
}
