package com.ita.if103java.ims.dto.warehouse.advice.associate;

import com.ita.if103java.ims.util.ListUtils;

import java.util.List;
import java.util.Optional;

public class BestWeightedAssociatesDto {
    private List<BestWeightedAssociateDto> suppliers;
    private List<BestWeightedAssociateDto> clients;

    public BestWeightedAssociatesDto() {
    }

    public BestWeightedAssociatesDto(List<BestWeightedAssociateDto> suppliers, List<BestWeightedAssociateDto> clients) {
        this.suppliers = suppliers;
        this.clients = clients;
    }

    public List<BestWeightedAssociateDto> getAssociates() {
        if (suppliers != null && clients != null) {
            return ListUtils.concat(suppliers, clients);
        }
        return Optional.ofNullable(suppliers).or(() -> Optional.ofNullable(clients)).orElse(null);
    }

    public List<BestWeightedAssociateDto> getClients() {
        return clients;
    }

    public void setClients(List<BestWeightedAssociateDto> clients) {
        this.clients = clients;
    }

    public List<BestWeightedAssociateDto> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<BestWeightedAssociateDto> suppliers) {
        this.suppliers = suppliers;
    }

    @Override
    public String toString() {
        return "BestWeightedAssociatesDto{" +
            "clients=" + clients +
            ", suppliers=" + suppliers +
            '}';
    }
}
