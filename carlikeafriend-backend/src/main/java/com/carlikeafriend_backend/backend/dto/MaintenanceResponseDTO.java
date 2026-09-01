package com.carlikeafriend_backend.backend.dto;

import java.time.LocalDate;

public class MaintenanceResponseDTO {
    private Long id;
    private String vehicleLicensePlate;
    private String maintenanceType;
    private String description;
    private Double cost;
    private Integer mileageAtMaintenance;
    private LocalDate maintenanceDate;
    private String technicianName;

    public MaintenanceResponseDTO() {
    }

    public MaintenanceResponseDTO(Long id,
                                  String vehicleLicensePlate,
                                  String maintenanceType,
                                  String description,
                                  Double cost,
                                  Integer mileageAtMaintenance,
                                  LocalDate maintenanceDate,
                                  String technicianName) {
        this.id = id;
        this.vehicleLicensePlate = vehicleLicensePlate;
        this.maintenanceType = maintenanceType;
        this.description = description;
        this.cost = cost;
        this.mileageAtMaintenance = mileageAtMaintenance;
        this.maintenanceDate = maintenanceDate;
        this.technicianName = technicianName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleLicensePlate() {
        return vehicleLicensePlate;
    }

    public void setVehicleLicensePlate(String vehicleLicensePlate) {
        this.vehicleLicensePlate = vehicleLicensePlate;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
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

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }
}
