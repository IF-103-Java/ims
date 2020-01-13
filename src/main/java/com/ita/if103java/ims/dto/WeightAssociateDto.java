package com.ita.if103java.ims.dto;

public class WeightAssociateDto {
    private Long associateId;
    private Double weight;

    public WeightAssociateDto() {
    }

    public WeightAssociateDto(Long associateId, Double weight) {
        this.associateId = associateId;
        this.weight = weight;
    }

    public Long getAssociateId() {
        return associateId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setAssociateId(Long associateId) {
        this.associateId = associateId;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getReverseWeight() {
        return 1.0 - getWeight();
    }

    @Override
    public String toString() {
        return "WeightAssociateDto{" +
            "associateId=" + associateId +
            ", weight=" + weight +
            ", reverseWeight=" + getReverseWeight() +
            '}';
    }
}
