package com.carlikeafriend_backend.backend.dto;

import java.time.LocalDateTime;

public class InspectionResponseDTO {

    private Long id;
    private String inspectionType;
    private Integer mileage;
    private Boolean hasDamage;
    private String damageDescription;
    private Integer fuelLevel;
    private String inspectorFullName; // Para la auditoría visual en el frontend
    private LocalDateTime createdAt;

    public InspectionResponseDTO() {
    }

    public InspectionResponseDTO(Long id,
                                 String inspectionType,
                                 Integer mileage,
                                 Boolean hasDamage,
                                 String damageDescription,
                                 Integer fuelLevel,
                                 String inspectorFullName,
                                 LocalDateTime createdAt) {
        this.id = id;
        this.inspectionType = inspectionType;
        this.mileage = mileage;
        this.hasDamage = hasDamage;
        this.damageDescription = damageDescription;
        this.fuelLevel = fuelLevel;
        this.inspectorFullName = inspectorFullName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
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

    public String getInspectorFullName() {
        return inspectorFullName;
    }

    public void setInspectorFullName(String inspectorFullName) {
        this.inspectorFullName = inspectorFullName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
