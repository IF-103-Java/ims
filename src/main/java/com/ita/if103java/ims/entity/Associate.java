package com.ita.if103java.ims.entity;

import java.util.Objects;

public class Associate {

    private Long id;
    private Long accountId;
    private String name;
    private String email;
    private String phone;
    private String additionalInfo;
    private AssociateType type;
    private boolean active;

    public Associate() {
    }

    public Associate(Long id, Long accountId, String name, String email, String phone, String additionalInfo, AssociateType type, boolean active) {
        this.id = id;
        this.accountId = accountId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.additionalInfo = additionalInfo;
        this.type = type;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public AssociateType getType() {
        return type;
    }

    public void setType(AssociateType type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Associate{" +
            "id=" + id +
            ", accountId=" + accountId +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", additionalInfo='" + additionalInfo + '\'' +
            ", type=" + type +
            ", active=" + active +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Associate associate = (Associate) o;
        return active == associate.active &&
            Objects.equals(id, associate.id) &&
            Objects.equals(accountId, associate.accountId) &&
            Objects.equals(name, associate.name) &&
            Objects.equals(email, associate.email) &&
            Objects.equals(phone, associate.phone) &&
            Objects.equals(additionalInfo, associate.additionalInfo) &&
            type == associate.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountId, name, email, phone, additionalInfo, type, active);
    }
}
