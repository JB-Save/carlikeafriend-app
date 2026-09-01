package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

public class VehicleDTO {

    @NotBlank(message = "La placa es obligatoria")
    @Pattern(regexp = "^[A-Za-z0-9]+([\\s\\-][A-Za-z0-9]+)*$", message = "La placa solo puede contener letras, números y guiones o espacios intermedios)")
    @Size(min = 4, max = 10, message = "La placa debe tener entre 4 y 10 caracteres")
    private String licensePlate;

    @NotBlank(message = "El VIN es obligatorio")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", flags = Pattern.Flag.CASE_INSENSITIVE, // Acepta minúsculas en la validación
            message = "El VIN debe tener exactamente 17 caracteres alfanuméricos (letras I, O, Q no están permitidas)")
    private String vin;

    @NotNull(message = "El kilometraje es obligatorio")
    @PositiveOrZero(message = "El  kilometraje no debe ser negativo")
    private Integer currentMileage;

    @NotBlank(message = "El color es obligatorio")
    @Size(max = 30, message = "El color no debe exceder los 30 caracteres")
    private String color;

    @NotNull(message = "El año es obligatorio")
    @PositiveOrZero(message = "El año no debe ser negativo")
    private Integer year;

    @NotNull(message = "El producto es obligatorio")
    private Long productId;

    @NotNull(message = "La sucursal es obligatoria")
    private Long currentBranchId;

    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 15, message = "El estado no debe exceder los 25 caracteres")
    private String status;

    public VehicleDTO() {
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Integer getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(Integer currentMileage) {
        this.currentMileage = currentMileage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCurrentBranchId() {
        return currentBranchId;
    }

    public void setCurrentBranchId(Long currentBranchId) {
        this.currentBranchId = currentBranchId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
