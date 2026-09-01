package com.carlikeafriend_backend.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class VehicleResponseDTO {
    private Long id;
    private String licensePlate;
    private String vin;
    private Integer currentMileage;
    private String color;
    private Integer year;
    private SimpleResponseDTO product;
    private SimpleResponseDTO currentBranch;
    private String status;

    public VehicleResponseDTO() {
    }

    public VehicleResponseDTO(Long id, String licensePlate, String vin, Integer currentMileage, String color, Integer year, SimpleResponseDTO product, SimpleResponseDTO currentBranch, String status) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.vin = vin;
        this.currentMileage = currentMileage;
        this.color = color;
        this.year = year;
        this.product = product;
        this.currentBranch = currentBranch;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public SimpleResponseDTO getProduct() {
        return product;
    }

    public void setProduct(SimpleResponseDTO product) {
        this.product = product;
    }

    public SimpleResponseDTO getCurrentBranch() {
        return currentBranch;
    }

    public void setCurrentBranch(SimpleResponseDTO currentBranch) {
        this.currentBranch = currentBranch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
