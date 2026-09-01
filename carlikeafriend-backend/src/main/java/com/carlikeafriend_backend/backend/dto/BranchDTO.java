package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class BranchDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe exceder los 100 caracteres")
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 100, message = "La dirección no debe exceder los 100 caracteres")
    private String address;

    @NotNull(message = "La ciudad es obligatoria")
    private Long cityId;

    @NotNull(message = "La latitud es requerida para ubicar el punto")
    @DecimalMin(value = "-90.0", message = "La latitud mínima permitida es -90.0")
    @DecimalMax(value = "90.0", message = "La latitud máxima permitida es 90.0")
    private BigDecimal latitude;

    @NotNull(message = "La longitud es requerida para ubicar el punto")
    @DecimalMin(value = "-180.0", message = "La longitud mínima permitida es -180.0")
    @DecimalMax(value = "180.0", message = "La longitud máxima permitida es 180.0")
    private BigDecimal longitude;

    public BranchDTO() {
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

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
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
