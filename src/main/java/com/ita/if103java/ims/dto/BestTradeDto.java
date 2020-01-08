package com.ita.if103java.ims.dto;

public class BestTradeDto {
    private Long associateId;
    private Double quantityPercentage;

    public BestTradeDto() {
    }

    public BestTradeDto(Long associateId, Double quantityPercentage) {
        this.associateId = associateId;
        this.quantityPercentage = quantityPercentage;
    }

    public Long getAssociateId() {
        return associateId;
    }

    public Double getQuantityPercentage() {
        return quantityPercentage;
    }

    public void setAssociateId(Long associateId) {
        this.associateId = associateId;
    }

    public void setQuantityPercentage(Double quantityPercentage) {
        this.quantityPercentage = quantityPercentage;
    }

    public Double getReverseQuantityPercentage() {
        return 1.0 - getQuantityPercentage();
    }
}
