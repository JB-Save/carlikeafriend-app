package com.carlikeafriend_backend.backend.dto;

public class BranchInventoryResponseDTO {
    private Long addonId;
    private String addonName;
    private Integer totalStock;

    public BranchInventoryResponseDTO() {
    }

    public BranchInventoryResponseDTO(Long addonId, String addonName, Integer totalStock) {
        this.addonId = addonId;
        this.addonName = addonName;
        this.totalStock = totalStock;
    }

    public Long getAddonId() {
        return addonId;
    }

    public void setAddonId(Long addonId) {
        this.addonId = addonId;
    }

    public String getAddonName() {
        return addonName;
    }

    public void setAddonName(String addonName) {
        this.addonName = addonName;
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }
}