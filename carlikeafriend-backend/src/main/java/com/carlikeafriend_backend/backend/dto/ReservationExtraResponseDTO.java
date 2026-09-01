package com.carlikeafriend_backend.backend.dto;

public class ReservationExtraResponseDTO {
    private Long addonId;
    private String addonName;
    private Integer quantity;
    private Double unitPrice;
    private String chargeType;
    private Integer maxChargeableDays;
    private Double subtotal;

    public ReservationExtraResponseDTO() {
    }

    public ReservationExtraResponseDTO(Long addonId, String addonName, Integer quantity, Double unitPrice, String chargeType, Integer maxChargeableDays, Double subtotal) {
        this.addonId = addonId;
        this.addonName = addonName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.chargeType = chargeType;
        this.maxChargeableDays = maxChargeableDays;
        this.subtotal = subtotal;
    }

    public Long getAddonId() {
        return addonId;
    }

    public void setAddonId(Long addonId) {
        this.addonId = addonId;
    }

    public java.lang.String getAddonName() {
        return addonName;
    }

    public void setAddonName(java.lang.String addonName) {
        this.addonName = addonName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
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

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
