package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class AddonDTO {
    @NotBlank(message = "El nombre del extra es obligatorio")
    @Size(max = 25, message = "El nombre no debe exceder los 25 caracteres")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100, message = "La descripción no debe exceder los 100 caracteres")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    private Double currentPrice;

    @NotBlank(message = "El tipo de cargo es obligatorio")
    @Size(max = 15, message = "El tipo de cargo no debe exceder los 15 caracteres")
    private String chargeType;

    @NotNull(message = "La cantidad máxima por reserva es obligatoria")
    @PositiveOrZero(message = "La cantidad máxima por reserva no puede ser negativa")
    private Integer maxQuantityPerReservation;

    @NotNull(message = "Los días máximos facturables es obligatorio")
    @PositiveOrZero(message = "Los días máximos facturables no puede ser negativo")
    private Integer maxChargeableDays;

    public AddonDTO() {
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

    public Integer getMaxQuantityPerReservation() {
        return maxQuantityPerReservation;
    }

    public void setMaxQuantityPerReservation(Integer maxQuantityPerReservation) {
        this.maxQuantityPerReservation = maxQuantityPerReservation;
    }

    public Integer getMaxChargeableDays() {
        return maxChargeableDays;
    }

    public void setMaxChargeableDays(Integer maxChargeableDays) {
        this.maxChargeableDays = maxChargeableDays;
    }
}
