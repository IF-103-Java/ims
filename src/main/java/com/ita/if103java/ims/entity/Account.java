package com.ita.if103java.ims.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;
import java.util.Objects;

public class Account {
    private Long id;
    private String name;
    private Long typeId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private ZonedDateTime createdDate;
    private boolean active;

    public Account() {

    }

    public Account(Long id, String name, Long typeId, ZonedDateTime createdDate, boolean active) {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
        this.createdDate = createdDate;
        this.active = active;
    }

    public Account(String name) {
        this.name = name;
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

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Account{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", typeId=" + typeId +
            ", createdDate=" + createdDate +
            ", active=" + active +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return isActive() == account.isActive() &&
            getName().equals(account.getName()) &&
            getTypeId().equals(account.getTypeId()) &&
            getCreatedDate().equals(account.getCreatedDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTypeId(), getCreatedDate(), isActive());
    }
}

