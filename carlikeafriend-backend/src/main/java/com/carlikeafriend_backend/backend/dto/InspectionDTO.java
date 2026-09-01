package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public class InspectionDTO {

    @NotNull(message = "La reserva es obligatoria")
    private UUID reservationId;

    @NotBlank(message = "El tipo de inspección (Pickup o Return) es obligatorio")
    @Size(max = 15, message = "El tipo de inspección no debe exceder los 15 caracteres")
    private String inspectionType;

    @NotNull(message = "El kilometraje es obligatorio")
    @PositiveOrZero(message = "El kilometraje no puede ser negativo")
    private Integer mileage;

    @NotNull(message = "Debe indicar si el vehículo presenta daños")
    private Boolean hasDamage;

    // Si hasDamage es true, este campo debería tener texto (lo validaremos en el servicio)
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 800, message = "La descripción no debe exceder los 800 caracteres")
    private String damageDescription;

    @NotNull(message = "El nivel de combustible es obligatorio")
    @Min(value = 0, message = "El nivel de combustible no puede ser menor a 0%")
    @Max(value = 100, message = "El nivel de combustible no puede superar el 100%")
    private Integer fuelLevel;

    public InspectionDTO() {
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public Boolean getHasDamage() {
        return hasDamage;
    }

    public void setHasDamage(Boolean hasDamage) {
        this.hasDamage = hasDamage;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public Integer getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(Integer fuelLevel) {
        this.fuelLevel = fuelLevel;
    }
}
