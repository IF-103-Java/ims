package com.ita.if103java.ims.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ita.if103java.ims.entity.AccountType;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class AccountDto {
    private Long id;
    private String name;
    private AccountType type;
    private Long typeId;
    private Long adminId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private ZonedDateTime createdDate;
    private boolean active;

    public AccountDto() {

    }

    public AccountDto(Long id, String name, AccountType type, Long typeId, Long adminId, ZonedDateTime createdDate, boolean active) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.typeId = typeId;
        this.adminId = adminId;
        this.createdDate = createdDate;
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

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
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
}
