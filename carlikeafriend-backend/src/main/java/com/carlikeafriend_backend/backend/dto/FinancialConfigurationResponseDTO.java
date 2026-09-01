package com.carlikeafriend_backend.backend.dto;

public class FinancialConfigurationResponseDTO {

    private Long id;
    private Double taxRate;
    private Double defaultTransferFee;
    private Double basicInsuranceDepositMultiplier;
    private Double premiumInsuranceDepositMultiplier;
    private Double fullCoverageDepositMultiplier;
    private Double insuranceBasicRate;
    private Double insurancePremiumRate;
    private Double insuranceFullCoverageRate;
    private Integer penaltyWindowHours;
    private Double cancellationPenaltyRate;
    private Double noShowPenaltyRate;
    private Integer maxRentalDays;

    public FinancialConfigurationResponseDTO() {}

    public FinancialConfigurationResponseDTO(Long id, Double taxRate, Double defaultTransferFee, Double basicInsuranceDepositMultiplier,
                                             Double premiumInsuranceDepositMultiplier, Double fullCoverageDepositMultiplier, Double insuranceBasicRate,
                                             Double insurancePremiumRate, Double insuranceFullCoverageRate, Integer penaltyWindowHours,Double cancellationPenaltyRate,
                                             Double noShowPenaltyRate, Integer maxRentalDays) {
        this.id = id;
        this.taxRate = taxRate;
        this.defaultTransferFee = defaultTransferFee;
        this.basicInsuranceDepositMultiplier = basicInsuranceDepositMultiplier;
        this.premiumInsuranceDepositMultiplier = premiumInsuranceDepositMultiplier;
        this.fullCoverageDepositMultiplier = fullCoverageDepositMultiplier;
        this.insuranceBasicRate = insuranceBasicRate;
        this.insurancePremiumRate = insurancePremiumRate;
        this.insuranceFullCoverageRate = insuranceFullCoverageRate;
        this.penaltyWindowHours = penaltyWindowHours;
        this.cancellationPenaltyRate = cancellationPenaltyRate;
        this.noShowPenaltyRate = noShowPenaltyRate;
        this.maxRentalDays = maxRentalDays;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Double getTaxRate() { return taxRate; }
    public void setTaxRate(Double taxRate) { this.taxRate = taxRate; }
    public Double getDefaultTransferFee() { return defaultTransferFee; }
    public void setDefaultTransferFee(Double defaultTransferFee) { this.defaultTransferFee = defaultTransferFee; }

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
    public Double getInsuranceBasicRate() { return insuranceBasicRate; }
    public void setInsuranceBasicRate(Double insuranceBasicRate) { this.insuranceBasicRate = insuranceBasicRate; }
    public Double getInsurancePremiumRate() { return insurancePremiumRate; }
    public void setInsurancePremiumRate(Double insurancePremiumRate) { this.insurancePremiumRate = insurancePremiumRate; }
    public Double getInsuranceFullCoverageRate() { return insuranceFullCoverageRate; }
    public void setInsuranceFullCoverageRate(Double insuranceFullCoverageRate) { this.insuranceFullCoverageRate = insuranceFullCoverageRate; }

    public Integer getPenaltyWindowHours() {
        return penaltyWindowHours;
    }

    public void setPenaltyWindowHours(Integer penaltyWindowHours) {
        this.penaltyWindowHours = penaltyWindowHours;
    }

    public Double getCancellationPenaltyRate() { return cancellationPenaltyRate; }
    public void setCancellationPenaltyRate(Double cancellationPenaltyRate) { this.cancellationPenaltyRate = cancellationPenaltyRate; }
    public Double getNoShowPenaltyRate() { return noShowPenaltyRate; }
    public void setNoShowPenaltyRate(Double noShowPenaltyRate) { this.noShowPenaltyRate = noShowPenaltyRate; }

    public Integer getMaxRentalDays() {
        return maxRentalDays;
    }

    public void setMaxRentalDays(Integer maxRentalDays) {
        this.maxRentalDays = maxRentalDays;
    }
}
