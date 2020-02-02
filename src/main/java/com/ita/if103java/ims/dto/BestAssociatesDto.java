package com.ita.if103java.ims.dto;

import java.util.List;

public class BestAssociatesDto {
    private List<BestAssociateDto> suppliers;
    private List<BestAssociateDto> clients;

    public BestAssociatesDto() {
    }

    public BestAssociatesDto(List<BestAssociateDto> suppliers, List<BestAssociateDto> clients) {
        this.suppliers = suppliers;
        this.clients = clients;
    }

    public List<BestAssociateDto> getClients() {
        return clients;
    }

    public void setClients(List<BestAssociateDto> clients) {
        this.clients = clients;
    }

    public List<BestAssociateDto> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<BestAssociateDto> suppliers) {
        this.suppliers = suppliers;
    }

    @Override
    public String toString() {
        return "BestAssociatesDto{" +
            "clients=" + clients +
            ", suppliers=" + suppliers +
            '}';
    }
}
