package com.ita.if103java.ims.dto;

import java.util.List;

public class BestAssociatesDto {
    private List<WeightedBestAssociateDto> suppliers;
    private List<WeightedBestAssociateDto> clients;

    public BestAssociatesDto() {
    }

    public BestAssociatesDto(List<WeightedBestAssociateDto> suppliers, List<WeightedBestAssociateDto> clients) {
        this.suppliers = suppliers;
        this.clients = clients;
    }

    public List<WeightedBestAssociateDto> getClients() {
        return clients;
    }

    public void setClients(List<WeightedBestAssociateDto> clients) {
        this.clients = clients;
    }

    public List<WeightedBestAssociateDto> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<WeightedBestAssociateDto> suppliers) {
        this.suppliers = suppliers;
    }

    @Override
    public String toString() {
        return "BestAssociatesDto{" +
            "clients=" + clients +
            ", suppliers=" + suppliers +
            '}';
    }

    public interface Weighed {
        double getWeight();
    }

    public static class WeightedBestAssociateDto implements Weighed {
        private BestAssociateDto associate;
        private double weight;

        public WeightedBestAssociateDto() {
        }

        public WeightedBestAssociateDto(BestAssociateDto associate, double weight) {
            this.associate = associate;
            this.weight = weight;
        }

        public BestAssociateDto getAssociate() {
            return associate;
        }

        public void setAssociate(BestAssociateDto associate) {
            this.associate = associate;
        }

        @Override
        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "WeightedBestAssociateDto{" +
                "associate=" + associate +
                ", weight=" + weight +
                '}';
        }
    }
}
