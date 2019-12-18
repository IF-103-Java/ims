package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.entity.AssociateType;

public class AssociateDto {

    private Long id;
    private Long accountId;
    private String name;
    private String email;
    private String phone;
    private String additionalInfo;
    private AssociateType type;
    private AddressDto addressDto;

    public AssociateDto() {
    }

    public AssociateDto(Long id, Long accountId, String name, String email, String phone, String additionalInfo, AssociateType type, AddressDto addressDto) {
        this.id = id;
        this.accountId = accountId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.additionalInfo = additionalInfo;
        this.type = type;
        this.addressDto = addressDto;
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

    public AddressDto getAddressDto() {
        return addressDto;
    }

    public void setAddressDto(AddressDto addressDto) {
        this.addressDto = addressDto;
    }

    @Override
    public String toString() {
        return "AssociateDto{" +
            "id=" + id +
            ", accountId=" + accountId +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", additionalInfo='" + additionalInfo + '\'' +
            ", type=" + type +
            ", addressDto=" + addressDto +
            '}';
    }
}
