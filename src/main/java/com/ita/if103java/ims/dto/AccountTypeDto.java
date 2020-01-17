package com.ita.if103java.ims.dto;

public class AccountTypeDto {
    private Long id;
    private String name;
    private Double price;
    private Integer level;
    private Integer maxWarehouses;
    private Integer maxWarehouseDepth;
    private Integer maxUsers;
    private Integer maxSuppliers;
    private Integer maxClients;
    private boolean deepWarehouseAnalytics;
    private boolean itemStorageAdvisor;
    private boolean active;

    public AccountTypeDto() {

    }

    public AccountTypeDto(Long id, String name, Double price, Integer level, Integer maxWarehouses, Integer maxWarehouseDepth, Integer maxUsers, Integer maxSuppliers, Integer maxClients, boolean deepWarehouseAnalytics, boolean itemStorageAdvisor, boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.level = level;
        this.maxWarehouses = maxWarehouses;
        this.maxWarehouseDepth = maxWarehouseDepth;
        this.maxUsers = maxUsers;
        this.maxSuppliers = maxSuppliers;
        this.maxClients = maxClients;
        this.deepWarehouseAnalytics = deepWarehouseAnalytics;
        this.itemStorageAdvisor = itemStorageAdvisor;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getMaxWarehouses() {
        return maxWarehouses;
    }

    public void setMaxWarehouses(Integer maxWarehouses) {
        this.maxWarehouses = maxWarehouses;
    }

    public Integer getMaxWarehouseDepth() {
        return maxWarehouseDepth;
    }

    public void setMaxWarehouseDepth(Integer maxWarhouseDepth) {
        this.maxWarehouseDepth = maxWarhouseDepth;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Integer getMaxSuppliers() {
        return maxSuppliers;
    }

    public void setMaxSuppliers(Integer maxSuppliers) {
        this.maxSuppliers = maxSuppliers;
    }

    public Integer getMaxClients() {
        return maxClients;
    }

    public void setMaxClients(Integer maxClients) {
        this.maxClients = maxClients;
    }

    public boolean isDeepWarehouseAnalytics() {
        return deepWarehouseAnalytics;
    }

    public void setDeepWarehouseAnalytics(boolean deepWarehouseAnalytics) {
        this.deepWarehouseAnalytics = deepWarehouseAnalytics;
    }

    public boolean isItemStorageAdvisor() {
        return itemStorageAdvisor;
    }

    public void setItemStorageAdvisor(boolean itemStorageAdvisor) {
        this.itemStorageAdvisor = itemStorageAdvisor;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
