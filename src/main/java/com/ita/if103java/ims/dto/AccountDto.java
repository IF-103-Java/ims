package com.ita.if103java.ims.dto;

import org.springframework.stereotype.Component;

@Component
public class AccountDto {
    private Long id;
    private String name;
    private Long typeId;
    private Long adminId;
    private boolean active;

    public AccountDto() {

    }

    public AccountDto(Long id, String name, Long typeId, Long adminId, boolean active) {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
        this.adminId = adminId;
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

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
