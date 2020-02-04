package com.ita.if103java.ims.dto.warehouse.advice.associate;

public class BestWeightedAssociateDto extends BestAssociateDto implements Weighted {
    private double weight;

    public BestWeightedAssociateDto() {
    }

    public BestWeightedAssociateDto(BestAssociateDto bestAssociateDto, double weight) {
        super(bestAssociateDto, bestAssociateDto.getTotalTransactionQuantity());
        this.weight = weight;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
