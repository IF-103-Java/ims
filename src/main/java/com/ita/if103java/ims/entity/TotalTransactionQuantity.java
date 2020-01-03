package com.ita.if103java.ims.entity;

public class TotalTransactionQuantity {
    private Long referenceId;
    private Long totalQuantity;

    public TotalTransactionQuantity(Long referenceId, Long totalQuantity) {
        this.referenceId = referenceId;
        this.totalQuantity = totalQuantity;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
