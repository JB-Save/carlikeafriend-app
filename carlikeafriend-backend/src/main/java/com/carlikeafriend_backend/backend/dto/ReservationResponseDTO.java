package com.carlikeafriend_backend.backend.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ReservationResponseDTO {
    private UUID id;
    private String reservationStatus;
    private LocalDateTime pickupDatetime;
    private LocalDateTime returnDatetime;
    private String pickupBranchNameSnapshot;
    private String returnBranchNameSnapshot;
    private String vehicleLicensePlateSnapshot;
    private String productNameSnapshot;
    private Double baseCost;
    private Double transferFee;
    private Double insuranceCost;
    private Double subtotal;
    private Double totalPrice;
    private String policiesSnapshot;
    private String policyHash;
    private Double cancellationPolicyAppliedSnapshot;
    private Integer fuelLevelAtPickupSnapshot;
    private Integer fuelLevelAtReturnSnapshot;
    private String arrivalFlightNumber;
    private Boolean isUserTheMainDriver;
    private Double extrasCost;
    private List<ReservationExtraResponseDTO> extras;
    private LocalDateTime expirationDate;

    // Desglose Financiero
    private Double taxAmount;
    private Double depositAmount;
    private String paymentStatus;
    private String paymentGatewayReference;

    public ReservationResponseDTO() {
    }

    public ReservationResponseDTO(UUID id,
                                  String reservationStatus,
                                  LocalDateTime pickupDatetime,
                                  LocalDateTime returnDatetime,
                                  String pickupBranchNameSnapshot,
                                  String returnBranchNameSnapshot,
                                  String vehicleLicensePlateSnapshot,
                                  String productNameSnapshot,
                                  Double baseCost,
                                  Double transferFee,
                                  Double insuranceCost,
                                  Double subtotal,
                                  Double totalPrice,
                                  String policiesSnapshot,
                                  String policyHash,
                                  Double cancellationPolicyAppliedSnapshot,
                                  Integer fuelLevelAtPickupSnapshot,
                                  Integer fuelLevelAtReturnSnapshot,
                                  String arrivalFlightNumber,
                                  Boolean isUserTheMainDriver,
                                  Double extrasCost,
                                  List<ReservationExtraResponseDTO> extras,
                                  LocalDateTime expirationDate,
                                  Double taxAmount,
                                  Double depositAmount,
                                  String paymentStatus,
                                  String paymentGatewayReference) {
        this.id = id;
        this.reservationStatus = reservationStatus;
        this.pickupDatetime = pickupDatetime;
        this.returnDatetime = returnDatetime;
        this.pickupBranchNameSnapshot = pickupBranchNameSnapshot;
        this.returnBranchNameSnapshot = returnBranchNameSnapshot;
        this.vehicleLicensePlateSnapshot = vehicleLicensePlateSnapshot;
        this.productNameSnapshot = productNameSnapshot;
        this.baseCost = baseCost;
        this.transferFee = transferFee;
        this.insuranceCost = insuranceCost;
        this.subtotal = subtotal;
        this.totalPrice = totalPrice;
        this.policiesSnapshot = policiesSnapshot;
        this.policyHash = policyHash;
        this.cancellationPolicyAppliedSnapshot = cancellationPolicyAppliedSnapshot;
        this.fuelLevelAtPickupSnapshot = fuelLevelAtPickupSnapshot;
        this.fuelLevelAtReturnSnapshot = fuelLevelAtReturnSnapshot;
        this.arrivalFlightNumber = arrivalFlightNumber;
        this.isUserTheMainDriver = isUserTheMainDriver;
        this.extrasCost = extrasCost;
        this.extras = extras;
        this.expirationDate = expirationDate;
        this.taxAmount = taxAmount;
        this.depositAmount = depositAmount;
        this.paymentStatus = paymentStatus;
        this.paymentGatewayReference = paymentGatewayReference;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public LocalDateTime getPickupDatetime() {
        return pickupDatetime;
    }

    public void setPickupDatetime(LocalDateTime pickupDatetime) {
        this.pickupDatetime = pickupDatetime;
    }

    public LocalDateTime getReturnDatetime() {
        return returnDatetime;
    }

    public void setReturnDatetime(LocalDateTime returnDatetime) {
        this.returnDatetime = returnDatetime;
    }

    public String getPickupBranchNameSnapshot() {
        return pickupBranchNameSnapshot;
    }

    public void setPickupBranchNameSnapshot(String pickupBranchNameSnapshot) {
        this.pickupBranchNameSnapshot = pickupBranchNameSnapshot;
    }

    public String getReturnBranchNameSnapshot() {
        return returnBranchNameSnapshot;
    }

    public void setReturnBranchNameSnapshot(String returnBranchNameSnapshot) {
        this.returnBranchNameSnapshot = returnBranchNameSnapshot;
    }

    public String getVehicleLicensePlateSnapshot() {
        return vehicleLicensePlateSnapshot;
    }

    public void setVehicleLicensePlateSnapshot(String vehicleLicensePlateSnapshot) {
        this.vehicleLicensePlateSnapshot = vehicleLicensePlateSnapshot;
    }

    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    public void setProductNameSnapshot(String productNameSnapshot) {
        this.productNameSnapshot = productNameSnapshot;
    }

    public Double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(Double baseCost) {
        this.baseCost = baseCost;
    }

    public Double getTransferFee() {
        return transferFee;
    }

    public void setTransferFee(Double transferFee) {
        this.transferFee = transferFee;
    }

    public Double getInsuranceCost() {
        return insuranceCost;
    }

    public void setInsuranceCost(Double insuranceCost) {
        this.insuranceCost = insuranceCost;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPoliciesSnapshot() {
        return policiesSnapshot;
    }

    public void setPoliciesSnapshot(String policiesSnapshot) {
        this.policiesSnapshot = policiesSnapshot;
    }

    public String getPolicyHash() {
        return policyHash;
    }

    public void setPolicyHash(String policyHash) {
        this.policyHash = policyHash;
    }

    public Double getCancellationPolicyAppliedSnapshot() {
        return cancellationPolicyAppliedSnapshot;
    }

    public void setCancellationPolicyAppliedSnapshot(Double cancellationPolicyAppliedSnapshot) {
        this.cancellationPolicyAppliedSnapshot = cancellationPolicyAppliedSnapshot;
    }

    public Integer getFuelLevelAtPickupSnapshot() {
        return fuelLevelAtPickupSnapshot;
    }

    public void setFuelLevelAtPickupSnapshot(Integer fuelLevelAtPickupSnapshot) {
        this.fuelLevelAtPickupSnapshot = fuelLevelAtPickupSnapshot;
    }

    public Integer getFuelLevelAtReturnSnapshot() {
        return fuelLevelAtReturnSnapshot;
    }

    public void setFuelLevelAtReturnSnapshot(Integer fuelLevelAtReturnSnapshot) {
        this.fuelLevelAtReturnSnapshot = fuelLevelAtReturnSnapshot;
    }

    public String getArrivalFlightNumber() {
        return arrivalFlightNumber;
    }

    public void setArrivalFlightNumber(String arrivalFlightNumber) {
        this.arrivalFlightNumber = arrivalFlightNumber;
    }

    public Boolean getUserTheMainDriver() {
        return isUserTheMainDriver;
    }

    public void setUserTheMainDriver(Boolean userTheMainDriver) {
        isUserTheMainDriver = userTheMainDriver;
    }

    public Double getExtrasCost() {
        return extrasCost;
    }

    public void setExtrasCost(Double extrasCost) {
        this.extrasCost = extrasCost;
    }

    public List<ReservationExtraResponseDTO> getExtras() {
        return extras;
    }

    public void setExtras(List<ReservationExtraResponseDTO> extras) {
        this.extras = extras;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(Double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentGatewayReference() {
        return paymentGatewayReference;
    }

    public void setPaymentGatewayReference(String paymentGatewayReference) {
        this.paymentGatewayReference = paymentGatewayReference;
    }
}
