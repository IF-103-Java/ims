package com.ita.if103java.ims.dto.warehouse.advice.associate;


public interface Weighted {
    double getWeight();

    default double getReverseWeight() {
        return 1 - getWeight();
    }
}
