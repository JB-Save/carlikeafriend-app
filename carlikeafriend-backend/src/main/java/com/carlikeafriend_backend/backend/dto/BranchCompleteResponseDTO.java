package com.carlikeafriend_backend.backend.dto;

import java.math.BigDecimal;

public class BranchCompleteResponseDTO {

    private Long id;
    private String name;
    private String address;
    private SimpleResponseDTO city;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public BranchCompleteResponseDTO() {
    }

    public BranchCompleteResponseDTO(Long id, String name, String address, SimpleResponseDTO city, BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public SimpleResponseDTO getCity() {
        return city;
    }

    public void setCity(SimpleResponseDTO city) {
        this.city = city;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}
