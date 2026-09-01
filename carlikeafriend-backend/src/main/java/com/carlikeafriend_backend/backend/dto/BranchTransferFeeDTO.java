package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class BranchTransferFeeDTO {

    @NotNull(message = "La sucursal de origen es obligatoria")
    private Long originBranchId;

    @NotNull(message = "La sucursal de destino es obligatoria")
    private Long destinationBranchId;

    @NotNull(message = "La tarifa es obligatoria")
    @PositiveOrZero(message = "El valor de la tarifa no debe ser negativo")
    private Double feeAmount;

    public BranchTransferFeeDTO() {
    }

    public Long getOriginBranchId() {
        return originBranchId;
    }

    public void setOriginBranchId(Long originBranchId) {
        this.originBranchId = originBranchId;
    }

    public Long getDestinationBranchId() {
        return destinationBranchId;
    }

    public void setDestinationBranchId(Long destinationBranchId) {
        this.destinationBranchId = destinationBranchId;
    }

    public Double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(Double feeAmount) {
        this.feeAmount = feeAmount;
    }
}
