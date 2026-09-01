package com.carlikeafriend_backend.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserReservationResponseDTO {
    private UUID id;
    private String reservationStatus;
    private LocalDateTime pickupDatetime;
    private LocalDateTime returnDatetime;
    private String pickupBranchNameSnapshot;
    private String returnBranchNameSnapshot;
    private String vehicleLicensePlateSnapshot;
    private Long productId;
    private String productNameSnapshot;
    private Double totalPrice;
    private Boolean hasReviewed;

    public UserReservationResponseDTO() {
    }

    public UserReservationResponseDTO(UUID id,
                                      String reservationStatus,
                                      LocalDateTime pickupDatetime,
                                      LocalDateTime returnDatetime,
                                      String pickupBranchNameSnapshot,
                                      String returnBranchNameSnapshot,
                                      String vehicleLicensePlateSnapshot,
                                      Long productId,
                                      String productNameSnapshot,
                                      Double totalPrice,
                                      Boolean hasReviewed) {
        this.id = id;
        this.reservationStatus = reservationStatus;
        this.pickupDatetime = pickupDatetime;
        this.returnDatetime = returnDatetime;
        this.pickupBranchNameSnapshot = pickupBranchNameSnapshot;
        this.returnBranchNameSnapshot = returnBranchNameSnapshot;
        this.vehicleLicensePlateSnapshot = vehicleLicensePlateSnapshot;
        this.productId = productId;
        this.productNameSnapshot = productNameSnapshot;
        this.totalPrice = totalPrice;
        this.hasReviewed = hasReviewed;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    public void setProductNameSnapshot(String productNameSnapshot) {
        this.productNameSnapshot = productNameSnapshot;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Boolean getHasReviewed() {
        return hasReviewed;
    }

    public void setHasReviewed(Boolean hasReviewed) {
        this.hasReviewed = hasReviewed;
    }
}
