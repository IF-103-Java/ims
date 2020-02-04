package com.ita.if103java.ims.dto.warehouse.advice.associate;

public class BestAssociateDto extends Associate {
    private Long totalTransactionQuantity;

    public BestAssociateDto() {
    }

    public BestAssociateDto(Associate associate, Long totalTransactionQuantity) {
        super(associate.getId(), associate.getName(), associate.getAddress(), associate.getType());
        this.totalTransactionQuantity = totalTransactionQuantity;
    }

    public Long getTotalTransactionQuantity() {
        return totalTransactionQuantity;
    }

    public void setTotalTransactionQuantity(Long totalTransactionQuantity) {
        this.totalTransactionQuantity = totalTransactionQuantity;
    }

}
