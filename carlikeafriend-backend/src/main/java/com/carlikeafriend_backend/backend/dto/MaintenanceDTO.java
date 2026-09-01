package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class MaintenanceDTO {
    @NotNull(message = "El tipo de mantenimiento es obligatorio")
    private Long maintenanceType;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 800, message = "La descripción no debe exceder los 800 caracteres")
    private String description;

    @NotNull(message = "El costo es obligatorio")
    @PositiveOrZero(message = "El costo no puede ser negativa")
    private Double cost;

    @NotNull(message = "El kilometraje es obligatorio")
    @PositiveOrZero(message = "El kilometraje no puede ser negativo")
    private Integer mileageAtMaintenance;

    @NotNull(message = "La fecha del mantenimiento es obligatoria")
    @FutureOrPresent(message = "La fecha del mantenimiento no puede ser en el pasado")
    private LocalDate maintenanceDate;

    public MaintenanceDTO() {
    }

    public Long getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(Long maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getMileageAtMaintenance() {
        return mileageAtMaintenance;
    }

    public void setMileageAtMaintenance(Integer mileageAtMaintenance) {
        this.mileageAtMaintenance = mileageAtMaintenance;
    }

    public LocalDate getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(LocalDate maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }
}
