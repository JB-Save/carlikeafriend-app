package com.carlikeafriend_backend.backend.dto;

public class AddonResponseDTO {
    private Long addonId;
    private String name;
    private String description;
    private Double currentPrice;
    private String chargeType; // PER_DAY o FLAT_FEE
    private Integer maxQuantityPerReservation;
    private Integer maxChargeableDays; // null si no tiene tope

    public AddonResponseDTO() {
    }

    public AddonResponseDTO(Long addonId, String name, String description, Double currentPrice, String chargeType, Integer maxQuantityPerReservation, Integer maxChargeableDays) {
        this.addonId = addonId;
        this.name = name;
        this.description = description;
        this.currentPrice = currentPrice;
        this.chargeType = chargeType;
        this.maxQuantityPerReservation = maxQuantityPerReservation;
        this.maxChargeableDays = maxChargeableDays;
    }

    public Long getAddonId() {
        return addonId;
    }

    public void setAddonId(Long addonId) {
        this.addonId = addonId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public Integer getMaxChargeableDays() {
        return maxChargeableDays;
    }

    public void setMaxChargeableDays(Integer maxChargeableDays) {
        this.maxChargeableDays = maxChargeableDays;
    }

    public Integer getMaxQuantityPerReservation() {
        return maxQuantityPerReservation;
    }

    public void setMaxQuantityPerReservation(Integer maxQuantityPerReservation) {
        this.maxQuantityPerReservation = maxQuantityPerReservation;
    }
}
