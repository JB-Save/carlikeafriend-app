package com.carlikeafriend_backend.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "financial_configuration")
public class FinancialConfiguration extends Auditable {

    @Id
    private Long id = 1L; // Forzamos a que siempre sea el ID 1 (Singleton)

    @Column(nullable = false)
    private Double taxRate;

    @Column(nullable = false)
    private Double defaultTransferFee;

    // Multiplicadores de riesgo sobre el Depósito Base del Producto
    @Column(nullable = false)
    private Double basicInsuranceDepositMultiplier;

    @Column(nullable = false)
    private Double premiumInsuranceDepositMultiplier;

    @Column(nullable = false)
    private Double fullCoverageDepositMultiplier;

    // Seguros
    @Column(nullable = false)
    private Double insuranceBasicRate;

    @Column(nullable = false)
    private Double insurancePremiumRate;

    @Column(nullable = false)
    private Double insuranceFullCoverageRate;

    // Penalidades
    @Column(nullable = false)
    private Integer penaltyWindowHours;

    @Column(nullable = false)
    private Double cancellationPenaltyRate;

    @Column(nullable = false)
    private Double noShowPenaltyRate;

    @Column(nullable = false)
    private Integer maxRentalDays; // Límite por defecto

    @Version
    private Long version;

    public FinancialConfiguration() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
