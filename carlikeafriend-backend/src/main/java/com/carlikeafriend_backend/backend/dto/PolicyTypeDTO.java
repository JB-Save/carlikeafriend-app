package com.carlikeafriend_backend.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PolicyTypeDTO {

    @NotBlank(message = "El tipo de política es obligatorio")
    @Size(max = 100, message = "El tipo de política no debe exceder los 100 caracteres")
    private String name;

    public PolicyTypeDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
