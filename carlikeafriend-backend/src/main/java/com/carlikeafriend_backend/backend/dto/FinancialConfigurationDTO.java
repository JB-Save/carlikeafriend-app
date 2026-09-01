package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class FinancialConfigurationDTO {

    @NotNull(message = "El IVA es obligatorio")
    @PositiveOrZero(message = "El IVA no puede ser negativo")
    private Double taxRate;

    @NotNull(message = "La Tarifa de transferencia predeterminada es obligatoria")
    @PositiveOrZero(message = "La Tarifa de transferencia predeterminada no puede ser negativa")
    private Double defaultTransferFee;

    @NotNull(message = "La tasa de depósito de seguro básico es obligatorio")
    @PositiveOrZero(message = "La tasa de depósito de seguro básico no puede ser negativo")
    private Double basicInsuranceDepositMultiplier;

    @NotNull(message = "La tasa de depósito de seguro premium es obligatorio")
    @PositiveOrZero(message = "La tasa de depósito de seguro premium no puede ser negativo")
    private Double premiumInsuranceDepositMultiplier;

    @NotNull(message = "La tasa de depósito de seguro covertura total es obligatorio")
    @PositiveOrZero(message = "La tasa de depósito de seguro covertura total no puede ser negativo")
    private Double fullCoverageDepositMultiplier;

    @NotNull(message = "La tarifa básica de seguro es obligatoria")
    @PositiveOrZero(message = "La tarifa básica de seguro no puede ser negativa")
    private Double insuranceBasicRate;

    @NotNull(message = "La tarifa de seguro premium es obligatoria")
    @PositiveOrZero(message = "La tarifa de seguro premium no puede ser negativa")
    private Double insurancePremiumRate;

    @NotNull(message = "La tarifa de seguro cobertura total es obligatoria")
    @PositiveOrZero(message = "La tarifa de seguro cobertura total no puede ser negativa")
    private Double insuranceFullCoverageRate;

    @NotNull(message = "La ventana en horas para penalidad es obligatoria")
    @PositiveOrZero(message = "La ventana en horas para penalidad no puede ser negativo")
    private Integer penaltyWindowHours;

    @NotNull(message = "La tasa de penalización por cancelación es obligatoria")
    @PositiveOrZero(message = "La tasa de penalización por cancelación no puede ser negativa")
    private Double cancellationPenaltyRate;

    @NotNull(message = "La tasa de penalización por no presentarse es obligatoria")
    @PositiveOrZero(message = "La tasa de penalización por no presentarse no puede ser negativa")
    private Double noShowPenaltyRate;

    @NotNull(message = "Los días máximo de alquiler es obligatorio")
    @PositiveOrZero(message = "Los días máximo de alquiler no puede ser negativo")
    private Integer maxRentalDays;

    public FinancialConfigurationDTO() {
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public Double getDefaultTransferFee() {
        return defaultTransferFee;
    }

    public void setDefaultTransferFee(Double defaultTransferFee) {
        this.defaultTransferFee = defaultTransferFee;
    }

    public Double getBasicInsuranceDepositMultiplier() {
        return basicInsuranceDepositMultiplier;
    }

    public void setBasicInsuranceDepositMultiplier(Double basicInsuranceDepositMultiplier) {
        this.basicInsuranceDepositMultiplier = basicInsuranceDepositMultiplier;
    }

    public Double getPremiumInsuranceDepositMultiplier() {
        return premiumInsuranceDepositMultiplier;
    }

    public void setPremiumInsuranceDepositMultiplier(Double premiumInsuranceDepositMultiplier) {
        this.premiumInsuranceDepositMultiplier = premiumInsuranceDepositMultiplier;
    }

    public Double getFullCoverageDepositMultiplier() {
        return fullCoverageDepositMultiplier;
    }

    public void setFullCoverageDepositMultiplier(Double fullCoverageDepositMultiplier) {
        this.fullCoverageDepositMultiplier = fullCoverageDepositMultiplier;
    }

    public Double getInsuranceBasicRate() {
        return insuranceBasicRate;
    }

    public void setInsuranceBasicRate(Double insuranceBasicRate) {
        this.insuranceBasicRate = insuranceBasicRate;
    }

    public Double getInsurancePremiumRate() {
        return insurancePremiumRate;
    }

    public void setInsurancePremiumRate(Double insurancePremiumRate) {
        this.insurancePremiumRate = insurancePremiumRate;
    }

    public Double getInsuranceFullCoverageRate() {
        return insuranceFullCoverageRate;
    }

    public void setInsuranceFullCoverageRate(Double insuranceFullCoverageRate) {
        this.insuranceFullCoverageRate = insuranceFullCoverageRate;
    }

    public Integer getPenaltyWindowHours() {
        return penaltyWindowHours;
    }

    public void setPenaltyWindowHours(Integer penaltyWindowHours) {
        this.penaltyWindowHours = penaltyWindowHours;
    }

    public Double getCancellationPenaltyRate() {
        return cancellationPenaltyRate;
    }

    public void setCancellationPenaltyRate(Double cancellationPenaltyRate) {
        this.cancellationPenaltyRate = cancellationPenaltyRate;
    }

    public Double getNoShowPenaltyRate() {
        return noShowPenaltyRate;
    }

    public void setNoShowPenaltyRate(Double noShowPenaltyRate) {
        this.noShowPenaltyRate = noShowPenaltyRate;
    }

    public Integer getMaxRentalDays() {
        return maxRentalDays;
    }

    public void setMaxRentalDays(Integer maxRentalDays) {
        this.maxRentalDays = maxRentalDays;
    }
}
