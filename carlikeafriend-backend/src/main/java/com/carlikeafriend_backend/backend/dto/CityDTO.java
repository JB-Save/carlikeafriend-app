package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CityDTO {

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no debe exceder los 100 caracteres")
    private String name;

    public CityDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
