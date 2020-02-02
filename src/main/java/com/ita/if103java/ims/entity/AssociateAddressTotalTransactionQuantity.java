package com.ita.if103java.ims.entity;

public class AssociateAddressTotalTransactionQuantity {
    private Long associateId;
    private String associateName;
    private AssociateType associateType;
    private String country;
    private String city;
    private String address;
    private Float latitude;
    private Float longitude;
    private Long totalTransactionQuantity;

    public AssociateAddressTotalTransactionQuantity() {
    }

    public AssociateAddressTotalTransactionQuantity(Long associateId, String associateName, AssociateType associateType,
                                                    String country, String city, String address,
                                                    Float latitude, Float longitude,
                                                    Long totalTransactionQuantity) {
        this.associateId = associateId;
        this.associateName = associateName;
        this.associateType = associateType;
        this.country = country;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalTransactionQuantity = totalTransactionQuantity;
    }

    public Long getAssociateId() {
        return associateId;
    }

    public void setAssociateId(Long associateId) {
        this.associateId = associateId;
    }

    public String getAssociateName() {
        return associateName;
    }

    public void setAssociateName(String associateName) {
        this.associateName = associateName;
    }

    public AssociateType getAssociateType() {
        return associateType;
    }

    public void setAssociateType(AssociateType associateType) {
        this.associateType = associateType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Long getTotalTransactionQuantity() {
        return totalTransactionQuantity;
    }

    public void setTotalTransactionQuantity(Long totalTransactionQuantity) {
        this.totalTransactionQuantity = totalTransactionQuantity;
    }

    @Override
    public String toString() {
        return "AssociateAddressTotalTransactionQuantity{" +
            "associateId=" + associateId +
            ", associateName='" + associateName + '\'' +
            ", associateType=" + associateType +
            ", country='" + country + '\'' +
            ", city='" + city + '\'' +
            ", address='" + address + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", totalTransactionQuantity=" + totalTransactionQuantity +
            '}';
    }
}
