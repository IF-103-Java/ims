package com.ita.if103java.ims.dto;

public class AccountCreateDto {
    private Long id;
    private String name;
    private Long adminId;

    public AccountCreateDto() {

    }

    public AccountCreateDto(Long id, String name, Long adminId) {
        this.id = id;
        this.name = name;
        this.adminId = adminId;
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

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
