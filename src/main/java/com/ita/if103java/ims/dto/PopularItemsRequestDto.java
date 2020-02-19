package com.ita.if103java.ims.dto;


import com.ita.if103java.ims.entity.DateType;
import com.ita.if103java.ims.entity.PopType;

public class PopularItemsRequestDto {
    private int quantity;
    private DateType dateType;
    private PopType popType;
    private String date;

    public PopularItemsRequestDto() {
    }

    public PopularItemsRequestDto(int quantity, DateType dateType, PopType popType, String date) {
        this.quantity = quantity;
        this.dateType = dateType;
        this.popType = popType;
        this.date = date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public DateType getDateType() {
        return dateType;
    }

    public void setDateType(DateType dateType) {
        this.dateType = dateType;
    }

    public PopType getPopType() {
        return popType;
    }

    public void setPopType(PopType popType) {
        this.popType = popType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
