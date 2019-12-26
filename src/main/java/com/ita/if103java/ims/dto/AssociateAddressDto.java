package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.entity.AssociateType;

public class AssociateAddressDto extends AddressDto {
    private Long associateId;
    private AssociateType associateType;

    public AssociateAddressDto() {
    }

    public AssociateAddressDto(Long id, String country, String city, String address, String zip,
                               Float latitude, Float longitude, Long associateId, AssociateType associateType) {
        super(id, country, city, address, zip, latitude, longitude);
        this.associateId = associateId;
        this.associateType = associateType;
    }

    public Long getAssociateId() {
        return associateId;
    }

    public void setAssociateId(Long associateId) {
        this.associateId = associateId;
    }

    public AssociateType getAssociateType() {
        return associateType;
    }

    public void setAssociateType(AssociateType associateType) {
        this.associateType = associateType;
    }

    @Override
    public String toString() {
        return "AssociateAddressDto{" +
            "associateId=" + associateId +
            ", associateType=" + associateType +
            ", id=" + getId() +
            ", country='" + getCountry() + '\'' +
            ", city='" + getCity() + '\'' +
            ", address='" + getAddress() + '\'' +
            ", zip='" + getZip() + '\'' +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            '}';
    }
}
