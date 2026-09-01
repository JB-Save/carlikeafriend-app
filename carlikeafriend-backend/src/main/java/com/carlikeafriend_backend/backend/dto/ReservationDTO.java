package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    @NotNull(message = "La sucursal de recogida es obligatoria")
    private Long pickupBranchId;

    @NotNull(message = "La sucursal de entrega es obligatoria")
    private Long returnBranchId;

    @NotNull(message = "La fecha de recogida es obligatoria")
    @FutureOrPresent(message = "La fecha de recogida no puede ser en el pasado")
    private LocalDateTime pickupDatetime;

    @NotNull(message = "La fecha de entrega es obligatoria")
    @Future(message = "La fecha de entrega debe ser en el futuro")
    private LocalDateTime returnDatetime;

    @NotBlank(message = "El tipo de seguro es obligatorio")
    @Size(max = 15, message = "El tipo de seguro no debe exceder los 15 caracteres")
    private String insuranceType;

    @Size(max = 20, message = "El número de vuelo no debe exceder los 20 caracteres")
    private String arrivalFlightNumber;

    @NotNull(message = "Debe especificar si el usuario es el conductor principal")
    private Boolean userTheMainDriver;

    // Se valida a nivel de servicio si es nulo cuando isUserTheMainDriver es false
    private DriverDetailsDTO driverDetails;

    private List<AddonRequestDTO> extras;

    public ReservationDTO() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getPickupBranchId() {
        return pickupBranchId;
    }

    public void setPickupBranchId(Long pickupBranchId) {
        this.pickupBranchId = pickupBranchId;
    }

    public Long getReturnBranchId() {
        return returnBranchId;
    }

    public void setReturnBranchId(Long returnBranchId) {
        this.returnBranchId = returnBranchId;
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

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getArrivalFlightNumber() {
        return arrivalFlightNumber;
    }

    public void setArrivalFlightNumber(String arrivalFlightNumber) {
        this.arrivalFlightNumber = arrivalFlightNumber;
    }

    public Boolean isUserTheMainDriver() {
        return userTheMainDriver;
    }

    public void setUserTheMainDriver(Boolean userTheMainDriver) {
        this.userTheMainDriver = userTheMainDriver;
    }

    public DriverDetailsDTO getDriverDetails() {
        return driverDetails;
    }

    public void setDriverDetails(DriverDetailsDTO driverDetails) {
        this.driverDetails = driverDetails;
    }

    public List<AddonRequestDTO> getExtras() {
        return extras;
    }

    public void setExtras(List<AddonRequestDTO> extras) {
        this.extras = extras;
    }
}
